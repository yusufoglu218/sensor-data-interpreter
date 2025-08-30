package com.turkishcargo.sensordatainterpreter.service;

import com.turkishcargo.sensordatainterpreter.dto.inbound.SensorDataDto;
import com.turkishcargo.sensordatainterpreter.dto.inbound.StatusChangeDto;
import com.turkishcargo.sensordatainterpreter.entity.sensor.OperationalSensorData;
import com.turkishcargo.sensordatainterpreter.entity.sensor.StatisticalSensorData;
import com.turkishcargo.sensordatainterpreter.mapper.SensorDataMapper;
import com.turkishcargo.sensordatainterpreter.repository.sensor.OperationalSensorDataRepository;
import com.turkishcargo.sensordatainterpreter.repository.sensor.StatisticalSensorDataRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorDataProcessingServiceTest {

    @Mock private StatisticalSensorDataRepository statisticalSensorDataRepository;
    @Mock private OperationalSensorDataRepository operationalSensorDataRepository;
    @Mock private CalibrationService calibrationService;
    @Mock private SensorDataMapper mapper;

    @InjectMocks
    private SensorDataProcessingService processingService;

    @Test
    @DisplayName("Should process a single message, calculate weight, and save both data types")
    void processSensorData_ShouldSaveAllDataCorrectly() {
        StatusChangeDto statusChange = new StatusChangeDto();
        statusChange.setAircraftId("TK-456");

        SensorDataDto testDataDto = new SensorDataDto();
        testDataDto.setId("msg-1");
        testDataDto.setCargoWeight(1000.0);
        testDataDto.setStatusChanges(List.of(statusChange));

        StatisticalSensorData mockReading = new StatisticalSensorData();
        OperationalSensorData mockEvent = new OperationalSensorData();
        mockEvent.setAircraftId("TK-456"); // Aircraft ID is needed for weight calculation

        when(mapper.toStatisticalSensorData(testDataDto)).thenReturn(mockReading);
        when(mapper.toOperationalSensorData(statusChange)).thenReturn(mockEvent);

        // 3. Mock the external service's behavior
        double calibrationFactor = 1.1;
        when(calibrationService.getCalibrationFactor("TK-456")).thenReturn(calibrationFactor);

        // Act
        processingService.processSensorData(testDataDto);

        // Assert
        // 1. Verify that the statistical data was saved
        verify(statisticalSensorDataRepository, times(1)).save(mockReading);

        // 2. Use an ArgumentCaptor to capture the list of events sent to the repository
        ArgumentCaptor<List<OperationalSensorData>> eventListCaptor = ArgumentCaptor.forClass(List.class);
        verify(operationalSensorDataRepository, times(1)).saveAll(eventListCaptor.capture());

        // 3. Inspect the captured list
        List<OperationalSensorData> capturedEvents = eventListCaptor.getValue();
        assertThat(capturedEvents).hasSize(1);

        OperationalSensorData savedEvent = capturedEvents.get(0);
        
        // 4. Verify that the weight was calculated and set correctly
        double expectedWeight = 1000.0 * 1.1;
        assertThat(savedEvent.getCalculatedWeight()).isEqualTo(expectedWeight);
    }

    @Test
    @DisplayName("Should not attempt to save events when status_changes is empty")
    void processSensorData_WhenNoStatusChanges_ShouldOnlySaveStatisticalData() {
        // Arrange
        SensorDataDto testDataDto = new SensorDataDto();
        testDataDto.setId("msg-2");
        testDataDto.setStatusChanges(List.of()); // Empty list

        StatisticalSensorData mockReading = new StatisticalSensorData();
        when(mapper.toStatisticalSensorData(testDataDto)).thenReturn(mockReading);

        // Act
        processingService.processSensorData(testDataDto);

        // Assert
        verify(statisticalSensorDataRepository, times(1)).save(mockReading);
        // Verify that saveAll was NEVER called on the operational repository
        verify(operationalSensorDataRepository, never()).saveAll(any());
    }
}