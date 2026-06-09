package com.insurance.claimmanagement.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "hospitals")
public class Hospital {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hospitalId;
    
    @Column(nullable = false, length = 100)
    private String hospitalName;
    
    @Column(nullable = false, length = 50)
    private String hospitalType; // Government, Private, Non-Profit
    
    @Column(nullable = false, length = 255)
    private String address;
    
    @Column(nullable = false, length = 20)
    private String phoneNumber;
    
    // One Hospital has Many Doctors
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Doctor> doctors = new ArrayList<>();
    
    // One Hospital has Many Treatments
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Treatment> treatments = new ArrayList<>();
    
    // Constructor
    public Hospital() {
    }
    
    public Hospital(String hospitalName, String hospitalType, String address, String phoneNumber) {
        this.hospitalName = hospitalName;
        this.hospitalType = hospitalType;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
    public Long getHospitalId() {
        return hospitalId;
    }
    
    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }
    
    public String getHospitalName() {
        return hospitalName;
    }
    
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
    
    public String getHospitalType() {
        return hospitalType;
    }
    
    public void setHospitalType(String hospitalType) {
        this.hospitalType = hospitalType;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public List<Doctor> getDoctors() {
        return doctors;
    }
    
    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }
    
    public List<Treatment> getTreatments() {
        return treatments;
    }
    
    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }
    
    @Override
    public String toString() {
        return "Hospital{" +
                "hospitalId=" + hospitalId +
                ", hospitalName='" + hospitalName + '\'' +
                ", hospitalType='" + hospitalType + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
