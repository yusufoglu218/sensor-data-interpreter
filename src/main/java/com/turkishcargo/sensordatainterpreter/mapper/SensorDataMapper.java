package com.turkishcargo.sensordatainterpreter.mapper;

import com.turkishcargo.sensordatainterpreter.dto.outbound.DeviceLocationDto;
import com.turkishcargo.sensordatainterpreter.dto.inbound.SensorDataDto;
import com.turkishcargo.sensordatainterpreter.dto.inbound.StatusChangeDto;
import com.turkishcargo.sensordatainterpreter.entity.sensor.OperationalSensorData;
import com.turkishcargo.sensordatainterpreter.entity.sensor.StatisticalSensorData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SensorDataMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deviceId", source = "dto", qualifiedByName = "deviceIdFromStatusChanges")
    StatisticalSensorData toStatisticalSensorData(SensorDataDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "calculatedWeight", ignore = true)
    @Mapping(target = "eventTime", source = "eventTime", qualifiedByName = "longToInstant")
    @Mapping(target = "latitude", source = "eventLocation.geometry.coordinates", qualifiedByName = "coordinatesToLatitude")
    @Mapping(target = "longitude", source = "eventLocation.geometry.coordinates", qualifiedByName = "coordinatesToLongitude")
    OperationalSensorData toOperationalSensorData(StatusChangeDto statusChangeDto);

    @Named("deviceIdFromStatusChanges")
    default String deviceIdFromStatusChanges(SensorDataDto dto) {
        if (dto.getStatusChanges() != null && !dto.getStatusChanges().isEmpty()) {
            return dto.getStatusChanges().getFirst().getDeviceId();
        }
        return null;
    }

    @Named("longToInstant")
    default Instant longToInstant(long epochSecond) {
        return Instant.ofEpochSecond(epochSecond);
    }

    @Named("coordinatesToLatitude")
    default Double coordinatesToLatitude(List<Double> coordinates) {
        if (coordinates != null && coordinates.size() == 2) {
            return coordinates.get(1); // [long, lat] -> lat
        }
        return null;
    }

    @Named("coordinatesToLongitude")
    default Double coordinatesToLongitude(List<Double> coordinates) {
        if (coordinates != null && coordinates.size() == 2) {
            return coordinates.getFirst(); // [long, lat] -> long
        }
        return null;
    }

    DeviceLocationDto toLocationHistoryDto(OperationalSensorData locationEvent);

    List<DeviceLocationDto> toLocationHistoryDtoList(List<OperationalSensorData> locationEvents);
}

