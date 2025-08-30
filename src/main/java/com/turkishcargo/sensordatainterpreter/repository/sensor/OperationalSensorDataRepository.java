package com.turkishcargo.sensordatainterpreter.repository.sensor;

import com.turkishcargo.sensordatainterpreter.entity.sensor.OperationalSensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OperationalSensorDataRepository extends JpaRepository<OperationalSensorData, Long> {

    List<OperationalSensorData> findByDeviceIdAndEventTimeBetweenOrderByEventTimeAsc(String deviceId, Instant startTime, Instant endTime);
}