package com.turkishcargo.sensordatainterpreter.service;

import com.turkishcargo.sensordatainterpreter.dto.inbound.SensorDataDto;
import com.turkishcargo.sensordatainterpreter.mapper.SensorDataMapper;
import com.turkishcargo.sensordatainterpreter.entity.sensor.OperationalSensorData;
import com.turkishcargo.sensordatainterpreter.entity.sensor.StatisticalSensorData;
import com.turkishcargo.sensordatainterpreter.repository.sensor.OperationalSensorDataRepository;
import com.turkishcargo.sensordatainterpreter.repository.sensor.StatisticalSensorDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorDataProcessingService {

    private final StatisticalSensorDataRepository statisticalSensorDataRepository;
    private final OperationalSensorDataRepository operationalSensorDataRepository;
    private final CalibrationService externalDataService;
    private final SensorDataMapper mapper;


    @Transactional
    public void processSensorData(SensorDataDto data) {
        try {
            StatisticalSensorData reading = mapper.toStatisticalSensorData(data);
            statisticalSensorDataRepository.save(reading);

            List<OperationalSensorData> events = prepareOperationalEvents(data);
            if (!events.isEmpty()) {
                throw new RuntimeException("Event can not be empty");
            }

            log.info("Processed message id={} -> 1 reading, {} events",
                    data.getId(), events.size());

        } catch (Exception e) {
            log.error("Error while processing single message id {}: {}",
                    data.getId(), e.getMessage(), e);
            throw e;
        }
    }

    private List<OperationalSensorData> prepareOperationalEvents(SensorDataDto data) {
        if (data.getStatusChanges() == null || data.getStatusChanges().isEmpty()) {
            return List.of();
        }

        return data.getStatusChanges().stream().map(statusChange -> {
            OperationalSensorData event = mapper.toOperationalSensorData(statusChange);

            double calibratedWeight = calculateCalibratedWeight(
                    event.getAircraftId(),
                    data.getCargoWeight()
            );

            event.setCalculatedWeight(calibratedWeight);

            return event;
        }).toList();
    }

    private double calculateCalibratedWeight(String aircraftId, Double rawCargoWeight) {
        double factor = externalDataService.getCalibrationFactor(aircraftId);
        double weight = (rawCargoWeight != null) ? rawCargoWeight : 0.0;
        return weight * factor;
    }
}
