package com.turkishcargo.sensordatainterpreter.controller;

import com.turkishcargo.sensordatainterpreter.dto.outbound.DeviceLocationDto;
import com.turkishcargo.sensordatainterpreter.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * Retrieves the location history for a specific device within a given time range.
     *
     * @param deviceId The unique identifier of the device.
     * @param startTime The start of the time range in ISO 8601 format (e.g., "2023-10-27T10:00:00Z").
     * @param endTime The end of the time range in ISO 8601 format.
     * @return A ResponseEntity containing a list of location history DTOs or a no-content status if none are found.
     */
    @GetMapping("/{deviceId}")
    public ResponseEntity<List<DeviceLocationDto>> getLocationHistory(
            @PathVariable String deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {

        List<DeviceLocationDto> deviceLocationDtoList = locationService.getLocationHistory(deviceId, startTime, endTime);

        if (deviceLocationDtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(deviceLocationDtoList);
    }
}