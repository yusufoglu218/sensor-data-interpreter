package com.turkishcargo.sensordatainterpreter.service;

import com.turkishcargo.sensordatainterpreter.dto.inbound.SensorDataDto;
import com.turkishcargo.sensordatainterpreter.dto.inbound.StatusChangeDto;
import com.turkishcargo.sensordatainterpreter.entity.sensor.OperationalSensorData;
import com.turkishcargo.sensordatainterpreter.entity.sensor.StatisticalSensorData;
import com.turkishcargo.sensordatainterpreter.mapper.SensorDataMapper;
import com.turkishcargo.sensordatainterpreter.repository.sensor.OperationalSensorDataRepository;
import com.turkishcargo.sensordatainterpreter.repository.sensor.StatisticalSensorDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorDataProcessingServiceTest {

    @Mock
    private StatisticalSensorDataRepository statisticalSensorDataRepository;
    @Mock
    private OperationalSensorDataRepository operationalSensorDataRepository;
    @Mock
    private CalibrationService calibrationService;
    @Mock
    private SensorDataMapper mapper;

    @InjectMocks
    private SensorDataProcessingService processingService;

    private SensorDataDto testMessage;

    @BeforeEach
    void setUp() {
        StatusChangeDto statusChange = new StatusChangeDto();
        statusChange.setAircraftId("TK-456");

        testMessage = new SensorDataDto();
        testMessage.setId("msg-1");
        testMessage.setCargoWeight(1000.0);
        testMessage.setStatusChanges(List.of(statusChange));
    }

    @Test
    @DisplayName("should process a single valid message, calculate weight, and save both data types")
    void processSensorData_whenMessageIsValid_shouldSaveAllData() {
        // Arrange
        StatisticalSensorData mockReading = new StatisticalSensorData();
        OperationalSensorData mockEvent = new OperationalSensorData();
        mockEvent.setAircraftId("TK-456");

        when(mapper.toStatisticalSensorData(any(SensorDataDto.class))).thenReturn(mockReading);
        when(mapper.toOperationalSensorData(any(StatusChangeDto.class))).thenReturn(mockEvent);

        double calibrationFactor = 1.1;
        when(calibrationService.getCalibrationFactor("TK-456")).thenReturn(calibrationFactor);

        processingService.processSensorData(testMessage);

        verify(statisticalSensorDataRepository, times(1)).save(mockReading);

        ArgumentCaptor<List<OperationalSensorData>> eventListCaptor = ArgumentCaptor.forClass(List.class);
        verify(operationalSensorDataRepository, times(1)).saveAll(eventListCaptor.capture());

        List<OperationalSensorData> capturedEvents = eventListCaptor.getValue();
        assertThat(capturedEvents).hasSize(1);
        OperationalSensorData savedEvent = capturedEvents.get(0);

        double expectedWeight = 1000.0 * 1.1;
        assertThat(savedEvent.getCalculatedWeight()).isEqualTo(expectedWeight);
    }

    @Test
    @DisplayName("should only save statistical data when status changes list is empty")
    void processSensorData_whenStatusChangesIsEmpty_shouldOnlySaveStatisticalData() {
        testMessage.setStatusChanges(Collections.emptyList()); // Override setup for this specific test

        StatisticalSensorData mockReading = new StatisticalSensorData();
        when(mapper.toStatisticalSensorData(testMessage)).thenReturn(mockReading);

        processingService.processSensorData(testMessage);

        verify(statisticalSensorDataRepository, times(1)).save(mockReading);

        verify(operationalSensorDataRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("should throw exception when a critical error occurs during processing")
    void processSensorData_whenRepositoryThrowsException_shouldPropagateException() {
        when(mapper.toStatisticalSensorData(testMessage)).thenThrow(new RuntimeException("Mapper Failed"));

        assertThatThrownBy(() -> processingService.processSensorData(testMessage))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Mapper Failed");

        verify(statisticalSensorDataRepository, never()).save(any());
        verify(operationalSensorDataRepository, never()).saveAll(any());
    }
}