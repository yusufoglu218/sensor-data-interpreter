package com.turkishcargo.sensordatainterpreter.service;

import com.turkishcargo.sensordatainterpreter.repository.calibration.AircraftCalibrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalibrationServiceImpl implements CalibrationService {

    private final AircraftCalibrationRepository calibrationRepository;
    private static final double DEFAULT_FACTOR = 1.0;

    /**
     * Fetches the calibration factor for a given aircraft from the external database.
     * If no specific factor is found for the aircraft, it returns a default factor of 1.0.
     *
     * @param aircraftId The unique identifier of the aircraft.
     * @return The calibration factor as a double.
     */
    @Override
    public double getCalibrationFactor(String aircraftId) {
        log.debug("Fetching calibration factor for aircraft: {}", aircraftId);
        return calibrationRepository.findByAircraftId(aircraftId)
                .map(calibration -> {
                    log.debug("Found factor {} for aircraft {}", calibration.getCalibrationFactor(), aircraftId);
                    return calibration.getCalibrationFactor();
                })
                .orElseGet(() -> {
                    log.warn("No calibration factor found for aircraft {}. Using default factor {}.", aircraftId, DEFAULT_FACTOR);
                    return DEFAULT_FACTOR;
                });
    }
}