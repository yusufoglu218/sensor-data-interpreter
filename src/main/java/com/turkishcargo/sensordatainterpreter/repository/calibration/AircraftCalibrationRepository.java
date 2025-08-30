package com.turkishcargo.sensordatainterpreter.repository.calibration;

import com.turkishcargo.sensordatainterpreter.entity.calibration.AircraftCalibration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AircraftCalibrationRepository extends JpaRepository<AircraftCalibration, Long> {
    Optional<AircraftCalibration> findByAircraftId(String aircraftId);
}