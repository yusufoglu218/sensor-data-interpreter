package com.turkishcargo.sensordatainterpreter.repository.sensor;

import com.turkishcargo.sensordatainterpreter.entity.sensor.StatisticalSensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticalSensorDataRepository extends JpaRepository<StatisticalSensorData, Long> {
}