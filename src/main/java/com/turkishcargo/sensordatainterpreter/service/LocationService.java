package com.turkishcargo.sensordatainterpreter.service;

import com.turkishcargo.sensordatainterpreter.dto.outbound.DeviceLocationDto;
import java.time.Instant;
import java.util.List;

public interface LocationService {
    List<DeviceLocationDto> getLocationHistory(String deviceId, Instant startTime, Instant endTime);
}