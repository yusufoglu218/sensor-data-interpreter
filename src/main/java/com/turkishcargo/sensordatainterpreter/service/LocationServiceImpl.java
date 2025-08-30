package com.turkishcargo.sensordatainterpreter.service;

import com.turkishcargo.sensordatainterpreter.dto.outbound.DeviceLocationDto;
import com.turkishcargo.sensordatainterpreter.mapper.SensorDataMapper;
import com.turkishcargo.sensordatainterpreter.entity.sensor.OperationalSensorData;
import com.turkishcargo.sensordatainterpreter.repository.sensor.OperationalSensorDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final OperationalSensorDataRepository operationalSensorDataRepository;
    private final SensorDataMapper mapper;

    /**
     * Retrieves a list of location history records for a given device within a specified time range.
     *
     * @param deviceId The unique identifier of the device.
     * @param startTime The start of the time range for the query.
     * @param endTime The end of the time range for the query.
     * @return A list of LocationHistoryDto objects, ordered by event time.
     */
    @Override
    public List<DeviceLocationDto> getLocationHistory(String deviceId, Instant startTime, Instant endTime) {
        log.info("Fetching location history for device: {} from {} to {}", deviceId, startTime, endTime);

        List<OperationalSensorData> events = operationalSensorDataRepository
                .findByDeviceIdAndEventTimeBetweenOrderByEventTimeAsc(deviceId, startTime, endTime);

        log.info("Found {} location events for device: {}", events.size(), deviceId);

        return mapper.toLocationHistoryDtoList(events);
    }
}