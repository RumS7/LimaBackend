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
        LeaveInfo.LeaveInfoBuilder leaveBuilder = LeaveInfo.builder()
                .user(user).fromDate(dto.getFromDate()).toDate(dto.getToDate())
                .reason(dto.getReason())
                .location(dto.getLocation())
                .status(LeaveStatus.PENDING);

        switch (user.getRank()) {
            case OR:
                leaveBuilder.pendingWithRank(Rank.JCO).pendingWithBty(user.getBty());
                break;
            case JCO:
                leaveBuilder.pendingWithRank(Rank.BC).pendingWithBty(user.getBty());
                break;
            case BC:
                leaveBuilder.pendingWithRank(Rank.CO).pendingWithBty(Bty.OC);
                break;
            case CO:
                leaveBuilder.status(LeaveStatus.APPROVED).approvedByCO(true);
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


        leaveInfo.setLocation(dto.getLocation());
        return leaveRepository.save(leaveInfo);
    }

    public LeaveInfo approveLeave(Long leaveId, Authentication auth) {
        UserInfo approver = userRepository.findByArmyId(auth.getName()).orElseThrow();
        LeaveInfo leaveInfo = leaveRepository.findById(leaveId).orElseThrow();

        if (leaveInfo.getStatus() != LeaveStatus.PENDING || approver.getRank() != leaveInfo.getPendingWithRank()) {
            throw new RuntimeException("Leave request is not pending your approval.");
        }
        if(approver.getRank() != Rank.CO && approver.getBty() != leaveInfo.getPendingWithBty()){
            throw new RuntimeException("You can only approve requests from your own team.");
        }

        switch (approver.getRank()) {
            case JCO:
                leaveInfo.setApprovedByJCO(true);
                leaveInfo.setPendingWithRank(Rank.BC);
                break;
            case BC:
                leaveInfo.setApprovedByBC(true);
                leaveInfo.setPendingWithRank(Rank.CO);
                leaveInfo.setPendingWithBty(Bty.OC);
                break;
            case CO:
                leaveInfo.setApprovedByCO(true);
                leaveInfo.setPendingWithRank(null);
                leaveInfo.setPendingWithBty(null);
                leaveInfo.setStatus(LeaveStatus.APPROVED);
                break;
        }
        return leaveRepository.save(leaveInfo);
    }
    public LeaveInfo rejectLeave(Long leaveId, Authentication auth) {
        // You might want to add logic here to ensure the rejecter has the authority
        LeaveInfo leaveInfo = leaveRepository.findById(leaveId).orElseThrow(() -> new RuntimeException("Leave request not found"));
        leaveInfo.setStatus(LeaveStatus.REJECTED);
        leaveInfo.setPendingWithRank(null);
        leaveInfo.setPendingWithBty(null);
        return leaveRepository.save(leaveInfo);
    }

    public List<LeaveInfo> getPendingForOfficer(Authentication auth) {
        UserInfo officer = userRepository.findByArmyId(auth.getName()).orElseThrow();
        if (officer.getRank() == Rank.CO) {
            return leaveRepository.findByPendingWithRank(Rank.CO);
        } else {
            return leaveRepository.findByPendingWithRankAndPendingWithBty(officer.getRank(), officer.getBty());
        }
    }

    public List<LeaveInfo> getMyLeaves(Authentication auth){
        UserInfo user = userRepository.findByArmyId(auth.getName()).orElseThrow();
        return leaveRepository.findByUser(user);
    }

    public List<LeaveInfo> getAllLeaves(){
        return leaveRepository.findAll();
    }

    public boolean isSoldierOnLeave(Authentication auth) {
        String armyId = auth.getName();
        UserInfo user = userRepository.findByArmyId(armyId).orElseThrow();
        LocalDate today = LocalDate.now();
        List<LeaveInfo> approvedLeaves = leaveRepository.findByUserAndStatus(user, LeaveStatus.APPROVED);

        for (LeaveInfo leave : approvedLeaves) {
            if (!today.isBefore(leave.getFromDate()) && !today.isAfter(leave.getToDate())) {
                return true;
            }
        }
        return false;
    }

    public List<LeaveInfo> getFinalizedLeavesForTeam(Authentication auth) {
        UserInfo officer = userRepository.findByArmyId(auth.getName()).orElseThrow();

        // Create a list of the statuses we consider "finalized"
        List<LeaveStatus> finalizedStatuses = List.of(LeaveStatus.APPROVED, LeaveStatus.REJECTED);

        // Use the new, more efficient repository method
        return leaveRepository.findByUser_BtyAndStatusIn(officer.getBty(), finalizedStatuses);
    }


    public Optional<LeaveInfo> getApprovedByRank(Long id){
        return leaveRepository.findById(id);
    }
}
