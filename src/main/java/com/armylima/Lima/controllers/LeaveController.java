package com.armylima.Lima.controllers;

import com.armylima.Lima.dto.LeaveRequestDTO;
import com.armylima.Lima.dto.UpdateLocationDTO;
import com.armylima.Lima.entities.LeaveInfo;
import com.armylima.Lima.dto.LeaveStatus;
import com.armylima.Lima.services.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/is-on-leave")
    public ResponseEntity<Boolean> isOnLeave(Authentication auth) {
        return ResponseEntity.ok(leaveService.isSoldierOnLeave(auth));
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
    public ResponseEntity<LeaveInfo> approveLeave(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(leaveService.approveLeave(id, auth));
    }

    @PreAuthorize("hasAnyRole('KING','BISHOP','KNIGHT','QUEEN','ROOK')")
    @PostMapping("/reject/{id}")
    public ResponseEntity<LeaveInfo> rejectLeave(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(leaveService.rejectLeave(id, auth));
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
}
