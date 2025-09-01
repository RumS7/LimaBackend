package com.armylima.Lima.controllers;

import com.armylima.Lima.dto.TransferBtyDTO;
import com.armylima.Lima.entities.UserInfo;
import com.armylima.Lima.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // It now accepts the Authentication principal to know who is asking.
    @PreAuthorize("hasRole('OFFICER')") // This correctly allows only OC and BC
    @GetMapping("/pending-verification")
    public ResponseEntity<List<UserInfo>> getPendingUsers(Authentication auth) {
        return ResponseEntity.ok(userService.getPendingUsers(auth));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserInfo> getCurrentUserProfile(Authentication auth) {
        return ResponseEntity.ok(
                userService.findByArmyId(auth.getName())
                        .orElseThrow(() -> new RuntimeException("User not found"))
        );
    }

    @PreAuthorize("hasRole('OFFICER')")
    @GetMapping("/verify/{armyId}")
    public ResponseEntity<UserInfo> verifyUser(
            @PathVariable String armyId

    ) {
        return ResponseEntity.ok(userService.verifyUser(armyId));
    }

    @PostMapping("/update-fcm-token")
    public ResponseEntity<?> updateFCMToken(@RequestBody Map<String,String> payload, Authentication auth){
        userService.updateFCMToken(auth.getName(), payload.get("token"));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{armyId}/transfer-bty")
    @PreAuthorize("hasAnyRole('KNIGHT')") // Only high command can transfer
    public ResponseEntity<UserInfo> transferUser(
            @PathVariable String armyId,
            @RequestBody TransferBtyDTO dto
    ) {
        return ResponseEntity.ok(userService.transferUserBty(armyId, dto.getNewBty()));
    }

    @GetMapping("/find/{armyId}")
    @PreAuthorize("hasRole('KNIGHT')")
    public ResponseEntity<UserInfo> findUserByArmyId(@PathVariable String armyId) {
        Optional<UserInfo> userOptional = userService.findByArmyId(armyId);

        // If user exists, return 200 OK with user data.
        // If not, return a 404 Not Found status.
        return userOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteOwnAccount(Authentication auth) {
        userService.deleteOwnAccount(auth);
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully."));
    }
}
