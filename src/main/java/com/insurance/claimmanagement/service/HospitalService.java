package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Hospital;
import java.util.List;
import java.util.Optional;

public interface HospitalService {
    Hospital saveHospital(Hospital hospital);
    Optional<Hospital> getHospitalById(Long hospitalId);
    List<Hospital> getAllHospitals();
    Hospital updateHospital(Hospital hospital);
    Hospital patchHospital(Long hospitalId, java.util.Map<String, Object> updates);
    boolean deleteHospital(Long hospitalId);
}
