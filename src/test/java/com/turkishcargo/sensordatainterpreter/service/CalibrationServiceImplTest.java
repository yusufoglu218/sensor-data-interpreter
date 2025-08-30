package com.turkishcargo.sensordatainterpreter.service;

import com.turkishcargo.sensordatainterpreter.entity.calibration.AircraftCalibration;
import com.turkishcargo.sensordatainterpreter.repository.calibration.AircraftCalibrationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalibrationServiceImplTest {

    @Mock
    private AircraftCalibrationRepository calibrationRepository;

    @InjectMocks
    private CalibrationServiceImpl calibrationService;

    private static final String EXISTING_AIRCRAFT_ID = "TK-123";
    private static final String NON_EXISTING_AIRCRAFT_ID = "TK-999";
    private static final double EXPECTED_FACTOR = 1.05;
    private static final double DEFAULT_FACTOR = 1.0;

    @Test
    @DisplayName("Should return specific factor when aircraft calibration exists in repository")
    void getCalibrationFactor_WhenAircraftExists_ShouldReturnFactor() {
        AircraftCalibration calibration = new AircraftCalibration();
        calibration.setAircraftId(EXISTING_AIRCRAFT_ID);
        calibration.setCalibrationFactor(EXPECTED_FACTOR);
        
        when(calibrationRepository.findByAircraftId(EXISTING_AIRCRAFT_ID)).thenReturn(Optional.of(calibration));

        double actualFactor = calibrationService.getCalibrationFactor(EXISTING_AIRCRAFT_ID);

        assertThat(actualFactor).isEqualTo(EXPECTED_FACTOR);
        verify(calibrationRepository, times(1)).findByAircraftId(EXISTING_AIRCRAFT_ID); // Verify repository was called once
    }

    @Test
    @DisplayName("Should return default factor when aircraft calibration does not exist in repository")
    void getCalibrationFactor_WhenAircraftDoesNotExist_ShouldReturnDefaultFactor() {
        when(calibrationRepository.findByAircraftId(NON_EXISTING_AIRCRAFT_ID)).thenReturn(Optional.empty());

        double actualFactor = calibrationService.getCalibrationFactor(NON_EXISTING_AIRCRAFT_ID);

        assertThat(actualFactor).isEqualTo(DEFAULT_FACTOR);
        verify(calibrationRepository, times(1)).findByAircraftId(NON_EXISTING_AIRCRAFT_ID);
    }
}