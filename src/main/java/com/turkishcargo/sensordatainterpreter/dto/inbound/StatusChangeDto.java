package com.turkishcargo.sensordatainterpreter.dto.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.turkishcargo.sensordatainterpreter.entity.enums.CargoType;
import com.turkishcargo.sensordatainterpreter.entity.enums.EventTypeReason;
import com.turkishcargo.sensordatainterpreter.entity.enums.EventType;
import lombok.Data;
import java.util.List;

@Data
public class StatusChangeDto {

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("aircraft_id")
    private String aircraftId;

    @JsonProperty("aircraft_type")
    private String aircraftType;

    @JsonProperty("cargo_type")
    private List<CargoType> cargoType;

    @JsonProperty("event_type")
    private EventType eventType;

    @JsonProperty("event_type_reason")
    private EventTypeReason eventTypeReason;

    @JsonProperty("event_time")
    private long eventTime;

    @JsonProperty("event_location")
    private EventLocationDto eventLocation;
}