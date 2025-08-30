package com.turkishcargo.sensordatainterpreter.service;

import com.turkishcargo.sensordatainterpreter.dto.outbound.DeviceLocationDto;
import com.turkishcargo.sensordatainterpreter.entity.sensor.OperationalSensorData;
import com.turkishcargo.sensordatainterpreter.mapper.SensorDataMapper;
import com.turkishcargo.sensordatainterpreter.repository.sensor.OperationalSensorDataRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private OperationalSensorDataRepository operationalSensorDataRepository;

    @Mock
    private SensorDataMapper mapper;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    @DisplayName("Should fetch events from repository and map them to DTOs")
    void getLocationHistory_ShouldReturnMappedDtoList() {
        // Arrange
        String deviceId = "device-01";
        Instant startTime = Instant.parse("2023-01-01T10:00:00Z");
        Instant endTime = Instant.parse("2023-01-01T12:00:00Z");

        OperationalSensorData event1 = new OperationalSensorData();
        List<OperationalSensorData> mockEvents = List.of(event1);

        DeviceLocationDto dto1 = new DeviceLocationDto();
        List<DeviceLocationDto> mockDtos = List.of(dto1);
        
        when(operationalSensorDataRepository.findByDeviceIdAndEventTimeBetweenOrderByEventTimeAsc(deviceId, startTime, endTime))
                .thenReturn(mockEvents);
        when(mapper.toLocationHistoryDtoList(mockEvents)).thenReturn(mockDtos);

        // Act
        List<DeviceLocationDto> result = locationService.getLocationHistory(deviceId, startTime, endTime);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto1);

        verify(operationalSensorDataRepository, times(1)).findByDeviceIdAndEventTimeBetweenOrderByEventTimeAsc(deviceId, startTime, endTime);
        verify(mapper, times(1)).toLocationHistoryDtoList(mockEvents);
    }
}