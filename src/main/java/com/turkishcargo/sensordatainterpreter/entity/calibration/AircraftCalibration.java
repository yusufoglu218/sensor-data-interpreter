package com.turkishcargo.sensordatainterpreter.entity.calibration;

import com.turkishcargo.sensordatainterpreter.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "aircraft_calibration")
public class AircraftCalibration extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String aircraftId;

    @Column(nullable = false)
    private Double calibrationFactor;
}