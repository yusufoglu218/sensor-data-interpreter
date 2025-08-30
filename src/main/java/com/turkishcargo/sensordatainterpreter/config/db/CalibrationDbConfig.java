package com.turkishcargo.sensordatainterpreter.config.db;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.turkishcargo.sensordatainterpreter.repository.calibration",
    entityManagerFactoryRef = "calibrationEntityManagerFactory",
    transactionManagerRef = "calibrationTransactionManager"
)
public class CalibrationDbConfig {

    @Bean(name = "calibrationDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.calibration")
    public DataSource calibrationDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "calibrationEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean calibrationEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(calibrationDataSource())
                .packages("com.turkishcargo.sensordatainterpreter.entity.calibration")
                .build();
    }

    @Bean(name = "calibrationTransactionManager")
    public PlatformTransactionManager calibrationTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(calibrationEntityManagerFactory(builder).getObject());
    }
}