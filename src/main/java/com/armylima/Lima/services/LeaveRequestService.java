package com.armylima.Lima.services;

import com.armylima.Lima.dto.*;
//import com.armylima.Lima.dto.Team;
import com.armylima.Lima.entities.LeaveInfo;
import com.armylima.Lima.entities.UserInfo;
import com.armylima.Lima.repositories.LeaveRequestRepository;
import java.time.LocalDate;

import com.armylima.Lima.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestService {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRepository;

    public LeaveRequestService(LeaveRequestRepository leaveRepository, UserRepository userRepository){
        this.userRepository = userRepository;
        this.leaveRepository = leaveRepository;
    }
    public LeaveInfo applyLeave(LeaveRequestDTO dto, Authentication auth) {
        UserInfo user = userRepository.findByArmyId(auth.getName()).orElseThrow();

        if (leaveRepository.existsByUserAndStatus(user, LeaveStatus.PENDING)) {
            throw new RuntimeException("You already have a pending leave request. Please wait for it to be processed.");
        }
        LeaveInfo.LeaveInfoBuilder leaveBuilder = LeaveInfo.builder()
                .user(user).fromDate(dto.getFromDate()).toDate(dto.getToDate())
                .reason(dto.getReason())
                .location(dto.getLocation())
                .status(LeaveStatus.PENDING);

        switch (user.getRank()) {
            case PAWN_SIPAHI:
                leaveBuilder.pendingWithRank(Rank.BISHOP).pendingWithBty(user.getBty());
                break;
            case ROOK, BISHOP: leaveBuilder.pendingWithRank(Rank.KNIGHT).pendingWithBty(user.getBty()); break;
            case KNIGHT:
                leaveBuilder.pendingWithRank(Rank.QUEEN).pendingWithBty(Bty.OC);
                break;

            case QUEEN:
                leaveBuilder.pendingWithRank(Rank.KING).pendingWithBty(Bty.OC); break;
            case KING:
                leaveBuilder.status(LeaveStatus.APPROVED).approvedByKing(true);
                break;
        }
        return leaveRepository.save(leaveBuilder.build());
    }

    public LeaveInfo updateLeaveLocation(Long leaveId, UpdateLocationDTO dto, Authentication auth) {

        UserInfo user = userRepository.findByArmyId(auth.getName()).orElseThrow();
        LeaveInfo leaveInfo = leaveRepository.findById(leaveId).orElseThrow();

        if(!leaveInfo.getUser().getArmyId().equals(user.getArmyId())){
            throw new RuntimeException("You can only update your own leave requests.");
        }

        LocalDate today= LocalDate.now();
        if(today.isBefore(leaveInfo.getFromDate()) || today.isAfter(leaveInfo.getToDate())){
            throw new RuntimeException("You can only update leave requests that are currently active.");
        }


        leaveInfo.setLocation(dto.getNewLocation());
        return leaveRepository.save(leaveInfo);
    }

//    public LeaveInfo approveLeave(Long leaveId, Authentication auth) {
//        UserInfo approver = userRepository.findByArmyId(auth.getName()).orElseThrow();
//        LeaveInfo leaveInfo = leaveRepository.findById(leaveId).orElseThrow();
//
//        if (leaveInfo.getStatus() != LeaveStatus.PENDING || approver.getRank() != leaveInfo.getPendingWithRank()) {
//            throw new RuntimeException("Leave request is not pending your approval.");
//        }
//        if(approver.getRank() != Rank.KING && approver.getRank() != Rank.QUEEN && approver.getBty() != leaveInfo.getPendingWithBty()){
//            throw new RuntimeException("You can only approve requests from your own team.");
//        }
//
//        switch (approver.getRank()) {
//            case BISHOP:
//                leaveInfo.setApprovedByBishop(true);
//                leaveInfo.setPendingWithRank(Rank.KNIGHT);
//                break;
//            case KNIGHT:
//                leaveInfo.setApprovedByKnight(true);
//                leaveInfo.setPendingWithRank(null);
//                leaveInfo.setPendingWithBty(null);
//                leaveInfo.setStatus(LeaveStatus.APPROVED);
//                break;
//            case QUEEN:
//                leaveInfo.setApprovedByQueen(true);
//                leaveInfo.setPendingWithRank(Rank.KING);
//
//                break;
//            case KING:
//                leaveInfo.setApprovedByKing(true);
//                leaveInfo.setPendingWithRank(null);
//                leaveInfo.setStatus(LeaveStatus.APPROVED);
//
//                break;
//        }
//        return leaveRepository.save(leaveInfo);
//    }


    public LeaveInfo approveLeave(Long leaveId,LeaveActionDTO dto, Authentication auth) {
        UserInfo approver = userRepository.findByArmyId(auth.getName()).orElseThrow();
        LeaveInfo leaveInfo = leaveRepository.findById(leaveId).orElseThrow(() -> new RuntimeException("Leave request not found"));
        UserInfo applicant = leaveInfo.getUser();
        leaveInfo.setRemarks(dto.getRemarks());

        if (leaveInfo.getStatus() != LeaveStatus.PENDING || approver.getRank() != leaveInfo.getPendingWithRank()) {
            throw new RuntimeException("Leave request is not pending your approval.");
        }
        if (List.of(Rank.KNIGHT, Rank.BISHOP).contains(approver.getRank()) && approver.getBty() != leaveInfo.getPendingWithBty()) {
            throw new RuntimeException("You can only approve requests from your own Bty.");
        }

        switch (approver.getRank()) {
            case BISHOP: leaveInfo.setApprovedByBishop(true); break;
            case KNIGHT: leaveInfo.setApprovedByKnight(true); break;
            case ROOK: leaveInfo.setApprovedByRook(true); break;
            case QUEEN: leaveInfo.setApprovedByQueen(true); break;
            case KING: leaveInfo.setApprovedByKing(true); break;
        }

        Rank nextApprover = getNextApprover(applicant.getRank(), approver.getRank());
        if (nextApprover == null) {
            leaveInfo.setStatus(LeaveStatus.APPROVED);
            leaveInfo.setPendingWithRank(null);
            leaveInfo.setPendingWithBty(null);
        } else {
            leaveInfo.setPendingWithRank(nextApprover);
            if (List.of(Rank.KING, Rank.QUEEN).contains(nextApprover)) {
                leaveInfo.setPendingWithBty(Bty.OC);
            } else if (nextApprover == Rank.KNIGHT && applicant.getRank() == Rank.ROOK) {
                // Special case for Rook -> Knight approval, knight might not be in a Bty
                leaveInfo.setPendingWithBty(Bty.OC);
            }
        }
        return leaveRepository.save(leaveInfo);
    }

    public LeaveInfo addLegacyLeave(LegacyLeaveDTO dto, Authentication auth) {
        UserInfo user = userRepository.findByArmyId(dto.getArmyId())
                .orElseThrow(() -> new RuntimeException("Soldier not found with ID: " + dto.getArmyId()));

        LeaveInfo leaveInfo = LeaveInfo.builder()
                .user(user)
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .reason(dto.getReason())
                .location(dto.getLocation())
                .status(LeaveStatus.APPROVED) // Legacy leaves are always considered approved
                .isLegacyRecord(true) // Mark as a legacy record
                .build();

        return leaveRepository.save(leaveInfo);
    }

    private Rank getNextApprover(Rank applicantRank, Rank currentApproverRank) {
        switch (applicantRank) {
            case PAWN_SIPAHI: return (currentApproverRank == Rank.BISHOP) ? Rank.KNIGHT : null;
            case BISHOP: return null;
            case ROOK:
                if (currentApproverRank == Rank.KNIGHT) return Rank.QUEEN;
                if (currentApproverRank == Rank.QUEEN) return Rank.KING;
                return null;
            case KNIGHT: return (currentApproverRank == Rank.QUEEN) ? Rank.KING : null;
            case QUEEN: return null;
            default: return null;
        }
    }




    public Optional<LeaveInfo> getApprovedByRank(Long id){
        return leaveRepository.findById(id);
    }


    public LeaveInfo rejectLeave(Long leaveId,LeaveActionDTO dto, Authentication auth) {
        UserInfo rejecter = userRepository.findByArmyId(auth.getName()).orElseThrow();
        LeaveInfo leaveInfo = leaveRepository.findById(leaveId).orElseThrow(() -> new RuntimeException("Leave request not found"));
        leaveInfo.setStatus(LeaveStatus.REJECTED);
        leaveInfo.setRemarks(dto.getRemarks());
        leaveInfo.setRejectedById(rejecter.getArmyId());
        leaveInfo.setPendingWithRank(null);
        leaveInfo.setPendingWithBty(null);
        return leaveRepository.save(leaveInfo);
    }

    public List<LeaveInfo> getPendingForOfficer(Authentication auth) {
        UserInfo officer = userRepository.findByArmyId(auth.getName()).orElseThrow();
        if (List.of(Rank.KING, Rank.QUEEN).contains(officer.getRank())) {
            return leaveRepository.findByPendingWithRank(officer.getRank());
        } else {
            return leaveRepository.findByPendingWithRankAndPendingWithBty(officer.getRank(), officer.getBty());
        }
    }

    public List<LeaveInfo> getLeaveHistoryForSubordinate(String armyId) {
        UserInfo user = userRepository.findByArmyId(armyId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + armyId));
        return leaveRepository.findByUser(user);
    }

    public List<LeaveInfo> getFinalizedLeavesForTeam(Authentication auth) {
        UserInfo officer = userRepository.findByArmyId(auth.getName()).orElseThrow();
        List<LeaveStatus> finalizedStatuses = List.of(LeaveStatus.APPROVED, LeaveStatus.REJECTED);
        return leaveRepository.findByUser_BtyAndStatusIn(officer.getBty(), finalizedStatuses);
    }

    public boolean isSoldierOnLeave(Authentication auth) {
        UserInfo user = userRepository.findByArmyId(auth.getName()).orElseThrow();
        LocalDate today = LocalDate.now();
        List<LeaveInfo> approvedLeaves = leaveRepository.findByUserAndStatus(user, LeaveStatus.APPROVED);
        return approvedLeaves.stream().anyMatch(leave -> !today.isBefore(leave.getFromDate()) && !today.isAfter(leave.getToDate()));
    }

    public List<LeaveInfo> getMyLeaves(Authentication auth) {
        UserInfo user = userRepository.findByArmyId(auth.getName()).orElseThrow();
        return leaveRepository.findByUser(user);
    }

    public List<LeaveInfo> getAllLeaves() {
        return leaveRepository.findAll();
    }

//    public LeaveInfo updateLocation(Long leaveId, UpdateLocationDTO dto, Authentication auth) {
//        UserInfo currentUser = userRepository.findByArmyId(auth.getName()).orElseThrow();
//        LeaveInfo leaveInfo = leaveRepository.findById(leaveId).orElseThrow(() -> new RuntimeException("Leave request not found"));
//
//        if (!leaveInfo.getUser().equals(currentUser)) {
//            throw new RuntimeException("You are not authorized to update this leave request.");
//        }
//        LocalDate today = LocalDate.now();
//        if (today.isBefore(leaveInfo.getFromDate()) || today.isAfter(leaveInfo.getToDate())) {
//            throw new RuntimeException("Location can only be updated during an active leave period.");
//        }
//
//        leaveInfo.setLocation(dto.getNewLocation());
//        return leaveRepository.save(leaveInfo);
//    }

    public Optional<LeaveInfo> findActiveLeaveForUser(Authentication auth) {
        UserInfo user = userRepository.findByArmyId(auth.getName()).orElseThrow();
        LocalDate today = LocalDate.now();

        return leaveRepository.findByUserAndStatus(user, LeaveStatus.APPROVED)
                .stream()
                .filter(leave -> !today.isBefore(leave.getFromDate()) && !today.isAfter(leave.getToDate()))
                .findFirst();
    }
}
