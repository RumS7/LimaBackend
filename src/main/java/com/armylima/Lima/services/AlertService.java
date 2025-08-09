package com.armylima.Lima.services;

import com.armylima.Lima.dto.Rank;
import com.armylima.Lima.entities.UserInfo;
import com.armylima.Lima.repositories.UserRepository;
import org.apache.catalina.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final UserRepository userRepository;
    private final FCMService fcmService;

    public AlertService(UserRepository userRepository, FCMService fcmService) {
        this.userRepository = userRepository;
        this.fcmService = fcmService;
    }

    public void triggerAlert(Authentication auth){

        UserInfo user= userRepository.findByArmyId(auth.getName()).orElseThrow();
        List<UserInfo> superiors= findSuperiors(user);

        for (UserInfo superior : superiors) {
            if (superior.getFcmToken() != null && !superior.getFcmToken().isEmpty()) {
                String title = "EMERGENCY ALERT";
                String body = "Alert triggered by " + user.getRank().name() + " " + user.getName() + " (ID: " + user.getArmyId() + ") from Bty " + user.getBty().name();
                fcmService.sendNotification(superior.getFcmToken(), title, body);
            }
        }
    }

    private List<UserInfo> findSuperiors(UserInfo user) {
        List<UserInfo> allUsers = userRepository.findAll();
        switch (user.getRank()) {
            case OR:
                return allUsers.stream().filter(u ->
                        (u.getRank() == Rank.JCO && u.getBty() == user.getBty()) ||
                                (u.getRank() == Rank.BC && u.getBty() == user.getBty()) ||
                                (u.getRank() == Rank.CO)
                ).collect(Collectors.toList());
            case JCO:
                return allUsers.stream().filter(u ->
                        (u.getRank() == Rank.BC && u.getBty() == user.getBty()) ||
                                (u.getRank() == Rank.CO)
                ).collect(Collectors.toList());
            case BC:
                return allUsers.stream().filter(u -> u.getRank() == Rank.CO).collect(Collectors.toList());
            default:
                return List.of();
        }
    }



    
}
