package com.armylima.Lima.services;


import com.armylima.Lima.dto.*;
import com.armylima.Lima.entities.HealthReport;
import com.armylima.Lima.entities.LeaveInfo;
import com.armylima.Lima.entities.UserInfo;
import com.armylima.Lima.repositories.HealthReportRepository;
import com.armylima.Lima.repositories.LeaveRequestRepository;
import com.armylima.Lima.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@Service
//public class AnalyticsService {
//
//    private final UserRepository userRepository;
//    private final LeaveRequestRepository leaveRequestRepository;
//
//    public AnalyticsService(UserRepository userRepository, LeaveRequestRepository leaveRequestRepository){
//        this.userRepository = userRepository;
//        this.leaveRequestRepository = leaveRequestRepository;
//    }
//
//
//    public List<SubordinateLeaveSummaryDTO> getSubordinateLeaveSummary(Authentication auth){
//
//        UserInfo officer= userRepository.findByArmyId(auth.getName()).orElseThrow();
//        List<UserInfo> allUsers= userRepository.findAll();
//        List<UserInfo> subordinates= new ArrayList<>();
//
//           switch(officer.getRole()){
//
//               case OC:
//                   subordinates= allUsers.stream()
//                           .filter(user->user.getRole()!= Role.OC)
//                           .toList();
//                    break;
//
//               case BC:
//                   subordinates= allUsers.stream()
//                           .filter(user->user.getTeam()==officer.getTeam()&& (user.getRole()==Role.JC || user.getRole()==Role.OR))
//                           .toList();
//                   break;
//
//               case JC:
//                   subordinates= allUsers.stream()
//                           .filter(user->user.getTeam()==officer.getTeam()&& (user.getRole()==Role.OR))
//                           .toList();
//                   break;
//
//           }
//
//           return subordinates.stream().map(subordinate->{
//               List<LeaveInfo> theirLeave= leaveRequestRepository.findByUserAndStatus(subordinate, LeaveStatus.APPROVED);
//               long totalLeaves= theirLeave.size();
//
//               Optional<LeaveInfo> lastLeave= theirLeave.stream()
//                       .max(Comparator.comparing(LeaveInfo::getFromDate));
//
//               boolean onLeave = lastLeave.isPresent() && !LocalDate.now().isAfter(lastLeave.get().getToDate()) && !LocalDate.now()
//                       .isBefore(lastLeave.get().getFromDate());
//
//               return new SubordinateLeaveSummaryDTO(
//                       subordinate,
//                       totalLeaves,
//                       lastLeave.map(LeaveInfo::getToDate).orElse(null),
//                       onLeave
//               );
//           }).collect(Collectors.toList());
//    }
//}



@Service
public class AnalyticsService {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRepository;
    private final HealthReportRepository healthRepository;

    public AnalyticsService(UserRepository userRepository, LeaveRequestRepository leaveRepository, HealthReportRepository healthRepository) {
        this.userRepository = userRepository;
        this.leaveRepository = leaveRepository;
        this.healthRepository = healthRepository;
    }

    public List<UserInfo> getSubordinates(UserInfo officer) {
        List<UserInfo> allUsers = userRepository.findAll();
        switch (officer.getRank()) {
            case CO:
                return allUsers.stream().filter(user -> user.getRank() != Rank.CO).collect(Collectors.toList());
            case BC:
                return allUsers.stream().filter(user -> user.getBty() == officer.getBty() && (user.getRank() == Rank.JCO || user.getRank() == Rank.OR)).collect(Collectors.toList());
            case JCO:
                return allUsers.stream().filter(user -> user.getBty() == officer.getBty() && user.getRank() == Rank.OR).collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    public List<SubordinateLeaveSummaryDTO> getSubordinateLeaveSummary(Authentication auth) {
        UserInfo officer = userRepository.findByArmyId(auth.getName()).orElseThrow();
        List<UserInfo> subordinates = getSubordinates(officer);

        return subordinates.stream().map(subordinate -> {
            List<LeaveInfo> theirLeaves = leaveRepository.findByUserAndStatus(subordinate, LeaveStatus.APPROVED);
            long totalLeaves = theirLeaves.size();
            Optional<LeaveInfo> lastLeave = theirLeaves.stream().max(Comparator.comparing(LeaveInfo::getToDate));
            boolean onLeave = lastLeave.isPresent() && !LocalDate.now().isAfter(lastLeave.get().getToDate()) && !LocalDate.now().isBefore(lastLeave.get().getFromDate());

            return new SubordinateLeaveSummaryDTO(subordinate, totalLeaves, lastLeave.map(LeaveInfo::getToDate).orElse(null), onLeave);
        }).collect(Collectors.toList());
    }

    public OnLeaveHealthSummaryDTO getOnLeaveHealthSummary(Authentication auth) {
        UserInfo officer = userRepository.findByArmyId(auth.getName()).orElseThrow();
        List<UserInfo> subordinates = getSubordinates(officer);
        LocalDate today = LocalDate.now();

        List<OnLeavePersonnelDTO> onLeavePersonnel = new ArrayList<>();
        int fitToday = 0;
        int notFitToday = 0;

        for (UserInfo subordinate : subordinates) {
            Optional<LeaveInfo> activeLeaveOpt = leaveRepository.findByUserAndStatus(subordinate, LeaveStatus.APPROVED).stream()
                    .filter(leave -> !today.isBefore(leave.getFromDate()) && !today.isAfter(leave.getToDate()))
                    .findFirst();

            if (activeLeaveOpt.isPresent()) {
                Optional<HealthReport> latestReportOpt = healthRepository.findByArmyIdAndReportDate(subordinate.getArmyId(), today);
                onLeavePersonnel.add(new OnLeavePersonnelDTO(subordinate, activeLeaveOpt.get(), latestReportOpt.orElse(null)));

                if (latestReportOpt.isPresent()) {
                    if (latestReportOpt.get().getStatus() == HealthStatus.FIT) {
                        fitToday++;
                    } else {
                        notFitToday++;
                    }
                }
            }
        }
        return new OnLeaveHealthSummaryDTO(onLeavePersonnel.size(), fitToday, notFitToday, onLeavePersonnel);
    }

    public List<LeaveInfo> getLeavesInLastDays(int days, Authentication auth) {
        UserInfo officer = userRepository.findByArmyId(auth.getName()).orElseThrow();
        List<UserInfo> subordinates = getSubordinates(officer);

        if (subordinates.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDate startDate = LocalDate.now().minusDays(days);

        return leaveRepository.findByUserInAndStatusAndFromDateAfter(
                subordinates,
                LeaveStatus.APPROVED,
                startDate
        );
    }

    public List<IndividualLeaveStatsDTO> getIndividualLeaveStats(Authentication auth){
        UserInfo officer = userRepository.findByArmyId(auth.getName()).orElseThrow();
        List<UserInfo> subordinates = getSubordinates(officer);

        final int MAX_LEAVE_DAYS= 90;
        LocalDate startOfYear=LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear=LocalDate.now().with(TemporalAdjusters.lastDayOfYear());

        return subordinates.stream().map(subordinate->{

            List<LeaveInfo> leavesThisYear= leaveRepository.findByUserAndStatus(subordinate, LeaveStatus.APPROVED)
                    .stream().filter(leave-> leave.getFromDate().isAfter(startOfYear)&& leave.getToDate().isBefore(LocalDate.now()))
                    .collect(Collectors.toList());


            int daysTaken = leavesThisYear.stream()
                    .mapToInt(leave -> (int) (ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1))
                    .sum();

            int daysRemaining= MAX_LEAVE_DAYS- daysTaken;

            return new IndividualLeaveStatsDTO(
                    subordinate,
                    daysTaken,
                    MAX_LEAVE_DAYS,
                    daysRemaining
            );
        }).collect(Collectors.toList());
    }
}