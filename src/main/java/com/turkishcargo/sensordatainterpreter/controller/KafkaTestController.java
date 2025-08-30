package com.turkishcargo.sensordatainterpreter.controller;

import com.turkishcargo.sensordatainterpreter.dto.inbound.SensorDataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/kafka")
public class KafkaTestController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaTestController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody SensorDataDto message) {
        kafkaTemplate.send("turkish-cargo-sensors", message.getId(), message);
        return ResponseEntity.ok("Message sent to Kafka: " + message.getId());
    }
}