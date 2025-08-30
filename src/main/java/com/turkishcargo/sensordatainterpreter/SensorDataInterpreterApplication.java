package com.turkishcargo.sensordatainterpreter;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Sensor Data Interpreter API",
				version = "1.0",
				description = "API for retrieving sensor data and location history"
		)
)
@SpringBootApplication
public class SensorDataInterpreterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SensorDataInterpreterApplication.class, args);
	}

}
