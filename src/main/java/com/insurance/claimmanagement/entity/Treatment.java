package com.insurance.claimmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "treatments")
public class Treatment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long treatmentId;
    
    @Column(nullable = false, length = 100)
    private String diagnosis;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String treatmentDescription;
    
    @Column(nullable = false)
    private Double treatmentAmount;
    
    @Column(nullable = false)
    private LocalDate treatmentDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-treatment")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "doctor_id", nullable = true)
    private Doctor doctor;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "hospital_id", nullable = true)
    private Hospital hospital;

    // Transient fields - used for capturing doctor/hospital info during claim submission
    // but NOT persisted in database (single source of truth is Doctor/Hospital entities)
    @Transient
    private String doctorName;

    @Transient
    private String doctorSpecialization;

    @Transient
    private String doctorQualification;

    @Transient
    private Integer doctorExperienceYears;

    @Transient
    private String hospitalName;

    @Transient
    private String hospitalAddress;

    @Transient
    private String hospitalPhone;
    
    // One Treatment has One Claim
    @OneToOne(mappedBy = "treatment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Claim claim;
    
    // Constructor
    public Treatment() {
    }
    
    public Treatment(String diagnosis, String treatmentDescription, Double treatmentAmount,
                    LocalDate treatmentDate, User user, Doctor doctor, Hospital hospital) {
        this.diagnosis = diagnosis;
        this.treatmentDescription = treatmentDescription;
        this.treatmentAmount = treatmentAmount;
        this.treatmentDate = treatmentDate;
        this.user = user;
        this.doctor = doctor;
        this.hospital = hospital;
    }
    
    // Getters and Setters
    public Long getTreatmentId() {
        return treatmentId;
    }
    
    public void setTreatmentId(Long treatmentId) {
        this.treatmentId = treatmentId;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public String getTreatmentDescription() {
        return treatmentDescription;
    }
    
    public void setTreatmentDescription(String treatmentDescription) {
        this.treatmentDescription = treatmentDescription;
    }
    
    public Double getTreatmentAmount() {
        return treatmentAmount;
    }
    
    public void setTreatmentAmount(Double treatmentAmount) {
        this.treatmentAmount = treatmentAmount;
    }
    
    public LocalDate getTreatmentDate() {
        return treatmentDate;
    }
    
    public void setTreatmentDate(LocalDate treatmentDate) {
        this.treatmentDate = treatmentDate;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Doctor getDoctor() {
        return doctor;
    }
    
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    
    public Hospital getHospital() {
        return hospital;
    }
    
    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public String getDoctorQualification() {
        return doctorQualification;
    }

    public void setDoctorQualification(String doctorQualification) {
        this.doctorQualification = doctorQualification;
    }

    public Integer getDoctorExperienceYears() {
        return doctorExperienceYears;
    }

    public void setDoctorExperienceYears(Integer doctorExperienceYears) {
        this.doctorExperienceYears = doctorExperienceYears;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getHospitalPhone() {
        return hospitalPhone;
    }

    public void setHospitalPhone(String hospitalPhone) {
        this.hospitalPhone = hospitalPhone;
    }
    
    public Claim getClaim() {
        return claim;
    }
    
    public void setClaim(Claim claim) {
        this.claim = claim;
    }
    
    @Override
    public String toString() {
        return "Treatment{" +
                "treatmentId=" + treatmentId +
                ", diagnosis='" + diagnosis + '\'' +
                ", treatmentAmount=" + treatmentAmount +
                ", treatmentDate=" + treatmentDate +
                '}';
    }
}
