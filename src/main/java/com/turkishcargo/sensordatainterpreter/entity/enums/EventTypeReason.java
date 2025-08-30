package com.turkishcargo.sensordatainterpreter.entity.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EventTypeReason {

    @JsonProperty("scheduled_loading")
    SCHEDULED_LOADING,

    @JsonProperty("unscheduled_loading")
    UNSCHEDULED_LOADING,
}
