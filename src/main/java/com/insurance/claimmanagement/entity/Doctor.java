package com.insurance.claimmanagement.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "doctors")
public class Doctor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;
    
    @Column(nullable = false, length = 100)
    private String doctorName;
    
    @Column(nullable = false, length = 50)
    private String specialization; // Cardiology, Orthopedics, etc.
    
    @Column(nullable = false, length = 50)
    private String qualification; // MBBS, MD, etc.
    
    @Column(nullable = false)
    private Integer experienceYears;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    @JsonBackReference("hospital-doctor")
    private Hospital hospital;
    
    // One Doctor has Many Treatments
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Treatment> treatments = new ArrayList<>();
    
    // Constructor
    public Doctor() {
    }
    
    public Doctor(String doctorName, String specialization, String qualification,
                 Integer experienceYears, Hospital hospital) {
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
        this.hospital = hospital;
    }
    
    // Getters and Setters
    public Long getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    
    public Integer getExperienceYears() {
        return experienceYears;
    }
    
    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }
    
    public Hospital getHospital() {
        return hospital;
    }
    
    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }
    
    public List<Treatment> getTreatments() {
        return treatments;
    }
    
    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }
    
    @Override
    public String toString() {
        return "Doctor{" +
                "doctorId=" + doctorId +
                ", doctorName='" + doctorName + '\'' +
                ", specialization='" + specialization + '\'' +
                "experienceYears=" + experienceYears +
                '}';
    }
}
