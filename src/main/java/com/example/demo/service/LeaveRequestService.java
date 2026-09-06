package com.example.demo.service;

import com.example.demo.entity.LeaveRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.LeaveRequestRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LeaveRequestService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_NOT_REQUIRED = "NOT_REQUIRED";
    private static final String STATUS_PARENTS_ACCEPTED = "PARENTS_ACCEPTED";

    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public LeaveRequestService(
            LeaveRequestRepository leaveRequestRepository,
            UserRepository userRepository,
            JavaMailSender mailSender) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> findAll() {
        return leaveRequestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public LeaveRequest findById(Long id) {
        return findLeaveRequestById(id);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> findByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return leaveRequestRepository.findByUser(user);
    }

    public LeaveRequest applyLeave(LeaveRequest request) {
        if (request.getUser() == null || request.getUser().getId() == null) {
            throw new IllegalArgumentException("User id is required");
        }

        User user = userRepository.findById(request.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with id: " + request.getUser().getId()));
        request.setUser(user);

        request.setParentStatus(STATUS_PENDING);
        request.setWardenStatus(STATUS_PENDING);
        request.setOverallStatus(STATUS_PENDING);

        if (request.isWorkingDay()) {
            request.setHodStatus(STATUS_PENDING);
        } else {
            request.setHodStatus(STATUS_NOT_REQUIRED);
        }

        return leaveRequestRepository.save(request);
        
    }

    public boolean requiresHodApproval(LeaveRequest request) {
        return request.isWorkingDay();
    }

    public boolean isVisibleToHod(LeaveRequest request) {
        if (!request.isWorkingDay()) {
            return false;
        }
    
        return !STATUS_APPROVED.equalsIgnoreCase(request.getOverallStatus())
                && !STATUS_REJECTED.equalsIgnoreCase(request.getOverallStatus());
    }
    
    public boolean isVisibleToWarden(LeaveRequest request) {
        return !STATUS_APPROVED.equalsIgnoreCase(request.getOverallStatus())
                && !STATUS_REJECTED.equalsIgnoreCase(request.getOverallStatus());
    }
        
    

    public String getReviewerMessage(LeaveRequest request) {
        if (STATUS_APPROVED.equalsIgnoreCase(request.getParentStatus())) {
            return "Parents Accepted";
        }
        return null;
    }

    public void sendToParentsMail(Long id) {
        LeaveRequest request = findLeaveRequestById(id);
        User student = resolveStudent(request);
    
        String studentName = student.getName();
        String regNo = student.getRoleNumber();
        String department = student.getDepartment();
    
        String acceptLink = "http://localhost:8080/api/leaves/"
                + id + "/parent-response?status=APPROVED";
    
        String rejectLink = "http://localhost:8080/api/leaves/"
                + id + "/parent-response?status=REJECTED";
    
        String subject = "Leave Request - Parent Approval Required";
    
        String body = "Dear Parent,\n\n"
                + "Your ward has submitted a leave request.\n\n"
                + "Student Name: " + studentName + "\n"
                + "Reg No: " + regNo + "\n"
                + "Department: " + department + "\n"
                + "Outing Type: " + request.getOutingType() + "\n"
                + "From: " + request.getStartDate() + "\n"
                + "To: " + request.getEndDate() + "\n"
                + "Reason: " + request.getReason() + "\n\n"
                + "Accept: " + acceptLink + "\n"
                + "Reject: " + rejectLink + "\n\n"
                + "Thank you.";
    
        String parentEmail = request.getFatherEmail();
    
        if (parentEmail != null && !parentEmail.isBlank()) {
            sendEmailIfPresent(parentEmail, subject, body);
        }
    }

    public LeaveRequest updateParentStatus(Long id, String status) {
        LeaveRequest request = findLeaveRequestById(id);
        request.setParentStatus(status);

        if (STATUS_APPROVED.equalsIgnoreCase(status)) {
            request.setOverallStatus(STATUS_PARENTS_ACCEPTED);
        } else if (STATUS_REJECTED.equalsIgnoreCase(status)) {
            request.setOverallStatus(STATUS_REJECTED);
        } else {
            request.setOverallStatus(STATUS_PENDING);
        }

        return leaveRequestRepository.save(request);
    }

    public LeaveRequest finalApprovalByHodOrWarden(Long id, String role, String status) {
        LeaveRequest request = findLeaveRequestById(id);
        User student = resolveStudent(request);

        if ("HOD".equalsIgnoreCase(role)) {
            if (!requiresHodApproval(request)) {
                throw new IllegalStateException("HOD approval is not required for holiday leave requests.");
            }
            request.setHodStatus(status);
        } else if ("WARDEN".equalsIgnoreCase(role)) {
            request.setWardenStatus(status);
        } else {
            throw new IllegalArgumentException("Invalid approver role: " + role);
        }

        recalculateOverallStatus(request);
        LeaveRequest savedRequest = leaveRequestRepository.save(request);

        if (STATUS_REJECTED.equalsIgnoreCase(status)) {
            sendRejectionEmailToStudent(student, savedRequest, role);
        } else if (STATUS_APPROVED.equalsIgnoreCase(status)) {
            if ("HOD".equalsIgnoreCase(role)) {
                sendPermissionGrantedEmailToStudent(student, savedRequest);
            } else if (isFullyApproved(savedRequest)) {
                sendPermissionGrantedEmailToStudent(student, savedRequest);
            }
        }

        return savedRequest;
    }

    public LeaveRequest updateHodStatus(Long id, String status) {
        return finalApprovalByHodOrWarden(id, "HOD", status);
    }

    public LeaveRequest updateWardenStatus(Long id, String status) {
        return finalApprovalByHodOrWarden(id, "WARDEN", status);
    }

    private LeaveRequest findLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with id: " + id));
    }

    private User resolveStudent(LeaveRequest request) {
        User user = request.getUser();
        if (user != null && user.getId() != null) {
            return userRepository.findById(user.getId()).orElse(user);
        }
        throw new IllegalArgumentException("Student details not found for leave request id: " + request.getId());
    }

    private String resolveDepartment(User student) {
        if (student.getHostelBlock() != null && !student.getHostelBlock().isBlank()) {
            return student.getHostelBlock();
        }
        return "Not Available";
    }

    private String resolveMotherEmail(LeaveRequest request, User student) {
        if (student.getParentEmail() != null && !student.getParentEmail().isBlank()) {
            return student.getParentEmail();
        }
        return null;
    }

    private void recalculateOverallStatus(LeaveRequest request) {
        if (isRejected(request.getParentStatus())
                || isRejected(request.getHodStatus())
                || isRejected(request.getWardenStatus())) {
            request.setOverallStatus(STATUS_REJECTED);
        } else if (isFullyApproved(request)) {
            request.setOverallStatus(STATUS_APPROVED);
        } else if (STATUS_APPROVED.equalsIgnoreCase(request.getParentStatus())) {
            request.setOverallStatus(STATUS_PARENTS_ACCEPTED);
        } else {
            request.setOverallStatus(STATUS_PENDING);
        }
    }

    private boolean isFullyApproved(LeaveRequest request) {
        if (!STATUS_APPROVED.equalsIgnoreCase(request.getParentStatus())) {
            return false;
        }

        if (requiresHodApproval(request)) {
            return STATUS_APPROVED.equalsIgnoreCase(request.getHodStatus())
                    && STATUS_APPROVED.equalsIgnoreCase(request.getWardenStatus());
        }

        return STATUS_APPROVED.equalsIgnoreCase(request.getWardenStatus());
    }

    private boolean isRejected(String status) {
        return STATUS_REJECTED.equalsIgnoreCase(status);
    }

    private void sendEmailIfPresent(String email, String subject, String body) {
        if (email == null || email.isBlank()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    private void sendPermissionGrantedEmailToStudent(User student, LeaveRequest request) {
        String subject = "Permission Granted - Leave Approved";
        String body = "Dear " + student.getName() + ",\n\n"
                + "Your leave request has been approved.\n\n"
                + "Name: " + student.getName() + "\n"
                + "Photo: [Student Photo Placeholder]\n"
                + "Reg No: " + student.getRoleNumber() + "\n"
                + "Department: " + resolveDepartment(student) + "\n"
                + "Leave ID: " + request.getId() + "\n\n"
                + "Permission Granted. Please show this email to the watchman for verification.\n\n"
                + "Thank you.";

        sendEmailIfPresent(student.getEmail(), subject, body);
    }

    private void sendRejectionEmailToStudent(User student, LeaveRequest request, String role) {
        String subject = "Leave Request Rejected";
        String body = "Dear " + student.getName() + ",\n\n"
                + "Your leave request has been rejected by " + role + ".\n\n"
                + "Name: " + student.getName() + "\n"
                + "Reg No: " + student.getRoleNumber() + "\n"
                + "Department: " + resolveDepartment(student) + "\n"
                + "Leave ID: " + request.getId() + "\n\n"
                + "Please contact the office for more details.\n\n"
                + "Thank you.";

        sendEmailIfPresent(student.getEmail(), subject, body);
    }
}
