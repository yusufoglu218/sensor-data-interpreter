package com.turkishcargo.sensordatainterpreter.entity.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EventType {
    @JsonProperty("loaded")
    LOADED,

    @JsonProperty("in_transit")
    IN_TRANSIT,

    @JsonProperty("unloaded")
    UNLOADED,

    @JsonProperty("delivered")
    DELIVERED
}