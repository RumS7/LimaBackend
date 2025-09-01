package com.armylima.Lima.controllers;

import com.armylima.Lima.dto.*;
import com.armylima.Lima.entities.LeaveInfo;
import com.armylima.Lima.services.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/leave")
public class LeaveController {

    private final LeaveRequestService leaveService;

    public LeaveController(LeaveRequestService leaveService) {
        this.leaveService = leaveService;
    }

    @PreAuthorize("hasAnyRole('PAWN_SIPAHI','KNIGHT','QUEEN','ROOK','BISHOP')")
    @PostMapping("/apply")
    public ResponseEntity<LeaveInfo> applyLeave(@RequestBody LeaveRequestDTO dto, Authentication auth) {
        return ResponseEntity.ok(leaveService.applyLeave(dto, auth));
    }

    @PreAuthorize("hasAnyRole('PAWN_SIPAHI','KNIGHT','QUEEN','ROOK','BISHOP')")
    @GetMapping("/my-requests")
    public ResponseEntity<List<LeaveInfo>> getMyRequests(Authentication auth) {
        return ResponseEntity.ok(leaveService.getMyLeaves(auth));
    }

    @PreAuthorize("hasAnyRole('KING','ROOK','QUEEN')") // Or whichever roles should see all leaves
    @GetMapping("/all")
    public ResponseEntity<List<LeaveInfo>> getAllRequests() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    @GetMapping("/current-active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LeaveInfo> getCurrentActiveLeave(Authentication auth) {
        // Find the user's active leave, if one exists
        Optional<LeaveInfo> activeLeave = leaveService.findActiveLeaveForUser(auth);
        // If found, return it with 200 OK. If not, return 204 No Content.
        return activeLeave.map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }


    @PreAuthorize("hasAnyRole('KNIGHT','BISHOP')")
    @GetMapping("/finalized-for-my-team")
    public ResponseEntity<List<LeaveInfo>> getFinalizedForMyTeam(Authentication auth) {
        return ResponseEntity.ok(leaveService.getFinalizedLeavesForTeam(auth));
    }

    @PreAuthorize("hasAnyRole('KING','BISHOP','KNIGHT','QUEEN','ROOK')")
    @GetMapping("/pending-for-me")
    public ResponseEntity<List<LeaveInfo>> getPendingForMe(Authentication auth) {
        return ResponseEntity.ok(leaveService.getPendingForOfficer(auth));
    }

    @PreAuthorize("hasAnyRole('KING','BISHOP','KNIGHT','QUEEN','ROOK')")
    @PostMapping("/approve/{id}")
    public ResponseEntity<LeaveInfo> approveLeave(@PathVariable Long id, @RequestBody LeaveActionDTO dto, Authentication auth) {
        return ResponseEntity.ok(leaveService.approveLeave(id,dto, auth));
    }

    @PreAuthorize("hasAnyRole('KING','BISHOP','KNIGHT','QUEEN','ROOK')")
    @PostMapping("/reject/{id}")
    public ResponseEntity<LeaveInfo> rejectLeave(@PathVariable Long id,@RequestBody LeaveActionDTO dto, Authentication auth) {
        return ResponseEntity.ok(leaveService.rejectLeave(id,dto, auth));
    }
    // --- NEW ENDPOINT ---
    @PostMapping("/add-legacy")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<LeaveInfo> addLegacyLeave(@RequestBody LegacyLeaveDTO dto, Authentication auth) {
        return ResponseEntity.ok(leaveService.addLegacyLeave(dto, auth));
    }

    @GetMapping("/history/{armyId}")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<List<LeaveInfo>> getLeaveHistoryForSubordinate(@PathVariable String armyId) {
        return ResponseEntity.ok(leaveService.getLeaveHistoryForSubordinate(armyId));
    }

    @PutMapping("/{id}/location")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LeaveInfo> updateLeaveLocation(
            @PathVariable Long id,
            @RequestBody UpdateLocationDTO dto,
            Authentication auth
            ){
        return ResponseEntity.ok(leaveService.updateLeaveLocation(id,dto,auth));
    }

    @PutMapping("/{id}/modify")
    @PreAuthorize("hasAnyRole('KNIGHT', 'QUEEN')")
    public ResponseEntity<LeaveInfo> modifyActiveLeave(
            @PathVariable Long id,
            @RequestBody ModifyLeaveDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(leaveService.modifyActiveLeave(id, dto, auth));
    }
}
