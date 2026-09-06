package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User user;
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String outingType; // Home / Local Outing

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false)
     
    private boolean isWorkingDay;

    private String fatherName;
    private String fatherPhone;
    private String fatherEmail;
    private String studentProfile;

    private String motherName;
    private String motherPhone;

    private String guardianName;
    private String guardianPhone;

    @Column(nullable = false)
    private String hodStatus = "PENDING";

    @Column(nullable = false)
    private String wardenStatus = "PENDING";

    @Column(nullable = false)
    private String parentStatus = "PENDING";

    @Column(nullable = false)
    private String overallStatus = "PENDING";

    @PrePersist
    protected void applyDefaults() {
        if (hodStatus == null) {
            hodStatus = "PENDING";
        }
        if (wardenStatus == null) {
            wardenStatus = "PENDING";
        }
        if (parentStatus == null) {
            parentStatus = "PENDING";
        }
        if (overallStatus == null) {
            overallStatus = "PENDING";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getOutingType() {
        return outingType;
    }

    public void setOutingType(String outingType) {
        this.outingType = outingType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isWorkingDay() {
        return isWorkingDay;
    }

    public void setWorkingDay(boolean workingDay) {
        isWorkingDay = workingDay;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherPhone() {
        return fatherPhone;
    }

    public void setFatherPhone(String fatherPhone) {
        this.fatherPhone = fatherPhone;
    }

    public String getFatherEmail() {
        return fatherEmail;
    }

    public void setFatherEmail(String fatherEmail) {
        this.fatherEmail = fatherEmail;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getMotherPhone() {
        return motherPhone;
    }

    public void setMotherPhone(String motherPhone) {
        this.motherPhone = motherPhone;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    public String getHodStatus() {
        return hodStatus;
    }

    public void setHodStatus(String hodStatus) {
        this.hodStatus = hodStatus;
    }

    public String getWardenStatus() {
        return wardenStatus;
    }

    public void setWardenStatus(String wardenStatus) {
        this.wardenStatus = wardenStatus;
    }

    public String getParentStatus() {
        return parentStatus;
    }

    public void setParentStatus(String parentStatus) {
        this.parentStatus = parentStatus;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }
    public String getStudentProfile() {
        return studentProfile;
    }
    
    public void setStudentProfile(String studentProfile) {
        this.studentProfile = studentProfile;
    }
}
