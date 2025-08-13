package com.armylima.Lima.repositories;

import com.armylima.Lima.dto.AccountStatus;
//import com.armylima.Lima.dto.Team;
import com.armylima.Lima.dto.Bty;
import com.armylima.Lima.dto.Rank;
import com.armylima.Lima.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  UserRepository  extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByEmail(String email);
    Optional<UserInfo> findByArmyId(String armyId);
    List<UserInfo> findByAccountStatusAndRank(AccountStatus status, Rank rank);
    public Boolean existsByEmail(String email);
    public Boolean existsByArmyId(String armyId);
    List<UserInfo> findByAccountStatusAndBty(AccountStatus status, Bty team);
}
