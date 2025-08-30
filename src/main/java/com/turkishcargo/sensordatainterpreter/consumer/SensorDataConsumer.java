package com.turkishcargo.sensordatainterpreter.consumer;

import com.turkishcargo.sensordatainterpreter.dto.inbound.SensorDataDto;
import com.turkishcargo.sensordatainterpreter.service.SensorDataProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensorDataConsumer {

    private final SensorDataProcessingService processingService;
    private final ThreadPoolExecutor sensorExecutor;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = "turkish-cargo-sensors",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(List<SensorDataDto> messages, Acknowledgment ack) {
        if (messages == null || messages.isEmpty()) return;

        log.info("Received batch of {} messages from Kafka", messages.size());

        for (SensorDataDto message : messages) {
            try {
                sensorExecutor.submit(() -> processMessage(message));
            } catch (RejectedExecutionException e) {
                log.warn("Executor queue full, processing message in caller thread: {}", message.getId());
                processMessage(message); // backpressure
            }
        }
        ack.acknowledge();
    }

    private void processMessage(SensorDataDto message) {
        try {
            processingService.processSensorData(message);
        } catch (Exception e) {
            log.error("Failed message id={}: {}", message.getId(), e.getMessage(), e);
            kafkaTemplate.send("turkish-cargo-sensors.dlq", message.getId(), message);
        }
    }

}
