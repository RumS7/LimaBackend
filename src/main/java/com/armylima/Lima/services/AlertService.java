package com.armylima.Lima.services;

import com.armylima.Lima.dto.Rank;
import com.armylima.Lima.entities.UserInfo;
import com.armylima.Lima.repositories.UserRepository;
import org.apache.catalina.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        String title = "EMERGENCY ALERT";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        String timestamp = LocalDateTime.now().format(formatter);


        String body = String.join("\n",
                "URGENT ASSISTANCE REQUIRED",
                "",
                "Personnel: " + user.getRank().name() + " " + user.getName(),
                "Army ID: " + user.getArmyId(),
                "Bty: " + user.getBty().name(),
                "Time: " + timestamp,
                "",
                "Please acknowledge and respond immediately."
        );


        for (UserInfo superior : superiors) {
            if (superior.getFcmToken() != null && !superior.getFcmToken().isEmpty()) {
                fcmService.sendNotification(superior.getFcmToken(), title, body);
            }
        }
    }

    private List<UserInfo> findSuperiors(UserInfo user) {
        List<UserInfo> allUsers = userRepository.findAll();
        switch (user.getRank()) {
            case PAWN_SIPAHI:
                return allUsers.stream().filter(u ->
                        (u.getRank() == Rank.BISHOP && u.getBty() == user.getBty()) ||
                                (u.getRank() == Rank.KNIGHT && u.getBty() == user.getBty()) ||
                                (u.getRank() == Rank.KING || u.getRank() == Rank.QUEEN || u.getRank() == Rank.ROOK)
                ).collect(Collectors.toList());
            case BISHOP:
                return allUsers.stream().filter(u ->
                        (u.getRank() == Rank.KNIGHT && u.getBty() == user.getBty()) ||
                                (u.getRank() == Rank.KING || u.getRank() == Rank.QUEEN ||  u.getRank() == Rank.ROOK)
                ).collect(Collectors.toList());
            case KNIGHT:
                return allUsers.stream().filter(u -> u.getRank() == Rank.KING || u.getRank() == Rank.QUEEN ||  u.getRank() == Rank.ROOK).collect(Collectors.toList());
            default:
                return List.of();
        }
    }



    
}
