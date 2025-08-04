package com.armylima.Lima.repositories;

import com.armylima.Lima.dto.Bty;
import com.armylima.Lima.dto.LeaveStatus;
import com.armylima.Lima.dto.Rank;
//import com.armylima.Lima.dto.Team;
import com.armylima.Lima.entities.LeaveInfo;
import com.armylima.Lima.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveInfo, Long> {


//    List<LeaveInfo> findByArmyId(String armyId);
//
//    List<LeaveInfo> findByArmyIdAndStatus(String armyId, LeaveStatus leaveStatus);

    List<LeaveInfo> findByStatus(LeaveStatus leaveStatus);

    List<LeaveInfo> findByPendingWithRank(Rank rank);
   // List<LeaveInfo> findByPendingWithRankAndStatus(Rank rank, LeaveStatus leaveStatus);

   // List<LeaveInfo> findByPendingWithRankAndPendingWithTeam(Rank rank, Team team);

    List<LeaveInfo> findByUser(UserInfo user);

    List<LeaveInfo> findByUserAndStatus(UserInfo user, LeaveStatus leaveStatus);
    List<LeaveInfo> findByPendingWithRankAndPendingWithBty(Rank rank, Bty team);

    List<LeaveInfo> findByUser_BtyAndStatusIn(Bty team,List<LeaveStatus> statuses);


    List<LeaveInfo> findByUserInAndStatusAndFromDateAfter(List<UserInfo> subordinates, LeaveStatus leaveStatus, LocalDate startDate);
}
