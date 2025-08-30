package com.turkishcargo.sensordatainterpreter.dto.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;


/**
 * Incoming sensor data from the kafka
 */
@Data
public class SensorDataDto {
    private String id;
    private String type;
    private Double temperature;
    private Double airPressure;
    private Double humidity;
    private Double lightLevel;
    private Double cargoWeight;
    private Double batteryCharge;
    private Double batteryVoltage;

    @JsonProperty("status_changes")
    private List<StatusChangeDto> statusChanges;}