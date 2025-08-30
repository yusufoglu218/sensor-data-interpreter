package com.turkishcargo.sensordatainterpreter.dto.inbound;

import lombok.Data;
import java.util.List;

@Data
public class GeometryDto {
    private String type;
    private List<Double> coordinates;
}
