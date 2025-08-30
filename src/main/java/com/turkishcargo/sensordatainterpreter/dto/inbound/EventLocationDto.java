package com.turkishcargo.sensordatainterpreter.dto.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EventLocationDto {

    @JsonProperty("geometry")
    private GeometryDto geometry;
}