package com.example.demo.controller;

import com.example.demo.entity.LeaveRequest;
import com.example.demo.service.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaves() {
        return ResponseEntity.ok(leaveRequestService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LeaveRequest>> getLeavesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(leaveRequestService.findByUserId(userId));
    }
    @GetMapping("/{id}/parent-response")
    public ResponseEntity<LeaveRequest> parentResponse(
        @PathVariable Long id,
        @RequestParam String status) {

    return ResponseEntity.ok(
            leaveRequestService.updateParentStatus(id, status)
    );
}

@PostMapping("/apply")
    public ResponseEntity<LeaveRequest> createLeave(@RequestBody LeaveRequest request) {
        return ResponseEntity.ok(leaveRequestService.applyLeave(request));
    }

    @PostMapping("/{id}/send-parent-mail")
    public ResponseEntity<Void> sendParentMail(@PathVariable Long id) {
        leaveRequestService.sendToParentsMail(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/parent-response")
    public ResponseEntity<LeaveRequest> handleParentResponse(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(leaveRequestService.updateParentStatus(id, status));
    }

    @PutMapping("/{id}/hod-approval")
    public ResponseEntity<LeaveRequest> approveByHod(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(leaveRequestService.finalApprovalByHodOrWarden(id, "HOD", status));
    }

    @PutMapping("/{id}/warden-approval")
    public ResponseEntity<LeaveRequest> approveByWarden(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(leaveRequestService.finalApprovalByHodOrWarden(id, "WARDEN", status));
    }
}
