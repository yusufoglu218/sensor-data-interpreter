package com.turkishcargo.sensordatainterpreter.entity.sensor;

import com.turkishcargo.sensordatainterpreter.entity.BaseEntity;
import com.turkishcargo.sensordatainterpreter.entity.enums.CargoType;
import com.turkishcargo.sensordatainterpreter.entity.enums.EventType;
import com.turkishcargo.sensordatainterpreter.entity.enums.EventTypeReason;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "operational_sensor_data")
public class OperationalSensorData extends BaseEntity {

    @Column(nullable = false)
    private String deviceId;

    private String aircraftId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventTypeReason eventTypeReason;

    @Column(nullable = false)
    private Instant eventTime;

    private Double latitude;
    private Double longitude;

    private Double calculatedWeight;
}