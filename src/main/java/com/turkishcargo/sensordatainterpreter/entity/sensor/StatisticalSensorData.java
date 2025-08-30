package com.turkishcargo.sensordatainterpreter.entity.sensor;

import com.turkishcargo.sensordatainterpreter.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "statistical_sensor_data")
public class StatisticalSensorData extends BaseEntity {

    @Column(nullable = false)
    private String deviceId;

    private Double temperature;
    private Double airPressure;
    private Double humidity;
    private Double lightLevel;
    private Double cargoWeight;
    private Double batteryCharge;
    private Double batteryVoltage;
}