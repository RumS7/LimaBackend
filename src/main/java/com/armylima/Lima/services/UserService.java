package com.armylima.Lima.services;

import com.armylima.Lima.dto.RegisterDTO;
import com.armylima.Lima.dto.AccountStatus;
import com.armylima.Lima.dto.Rank;
import com.armylima.Lima.entities.UserInfo;
import com.armylima.Lima.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(RegisterDTO dto){
        try {
            UserInfo user = new UserInfo();
            user.setEmail(dto.email());
            user.setPassword(passwordEncoder.encode(dto.password()));
            user.setName(dto.name());
            user.setArmyId(dto.armyId());
            user.setBty(dto.bty());
            user.setRank(dto.rank());

            // Corrected: JCOs are officers and should be active on creation.
            if(dto.rank() == Rank.CO || dto.rank() == Rank.BC || dto.rank() == Rank.JCO){
                user.setAccountStatus(AccountStatus.ACTIVE);
            } else {
                user.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
            }
            userRepository.save(user);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    public UserInfo verifyUser(String armyId) {
        UserInfo user = userRepository.findByArmyId(armyId)
                .orElseThrow(() -> new RuntimeException("User not found "+ armyId));
        user.setAccountStatus(AccountStatus.ACTIVE);
        return userRepository.save(user);
    }

    // --- THIS METHOD IS UPDATED WITH THE NEW LOGIC ---
    public List<UserInfo> getPendingUsers(Authentication auth) {
        String armyId = auth.getName();
        UserInfo approver = userRepository.findByArmyId(armyId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        // If the approver is the OC, show all pending users from all teams.
        if (approver.getRank() == Rank.CO) {
            return userRepository.findByAccountStatus(AccountStatus.PENDING_VERIFICATION);
        }

        // If the approver is a BC, only show pending users from their own team.
        if (approver.getRank() == Rank.BC) {
            return userRepository.findByAccountStatusAndBty(
                    AccountStatus.PENDING_VERIFICATION,
                    approver.getBty()
            );
        }

        // Other roles (like JC) cannot see any pending users.
        return Collections.emptyList();
    }

    public Optional<UserInfo> findByEmail(String email){
        return userRepository.findByEmail(email);

    }

    public Optional<UserInfo> findByArmyId(String armyId){
        return userRepository.findByArmyId(armyId);
    }

    public List<UserInfo> getAllUsers(){
        return userRepository.findAll();
    }

    public void updateFCMToken(String armyId, String token){
        userRepository.findByArmyId(armyId).ifPresent(user->{
            user.setFcmToken(token);
            userRepository.save(user);
        });
    }

}
