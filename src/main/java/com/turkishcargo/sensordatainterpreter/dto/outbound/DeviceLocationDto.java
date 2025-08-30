package com.turkishcargo.sensordatainterpreter.dto.outbound;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class DeviceLocationDto {
    private String eventType;
    private String eventTypeReason;
    private Instant eventTime;
    private Double latitude;
    private Double longitude;
}