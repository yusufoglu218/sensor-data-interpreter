package com.turkishcargo.sensordatainterpreter.config.db;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.turkishcargo.sensordatainterpreter.repository.sensor",
    entityManagerFactoryRef = "sensorEntityManagerFactory",
    transactionManagerRef = "sensorTransactionManager"
)
public class SensorDbConfig {

    @Primary
    @Bean(name = "sensorDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sensor")
    public DataSource sensorDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "sensorEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sensorEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(sensorDataSource())
                .packages("com.turkishcargo.sensordatainterpreter.entity.sensor")
                .build();
    }

    @Primary
    @Bean(name = "sensorTransactionManager")
    public PlatformTransactionManager sensorTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(sensorEntityManagerFactory(builder).getObject());
    }
}