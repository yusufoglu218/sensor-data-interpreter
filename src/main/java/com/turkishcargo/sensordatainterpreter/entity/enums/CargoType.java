package com.turkishcargo.sensordatainterpreter.entity.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CargoType {
    @JsonProperty("perishable")
    PERISHABLE,

    @JsonProperty("electronics")
    ELECTRONICS,

    @JsonProperty("dangerous_goods")
    DANGEROUS_GOODS,

    @JsonProperty("general")
    GENERAL
}
