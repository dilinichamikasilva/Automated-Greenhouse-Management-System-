package lk.ijse.sensorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorDTO {
    private Double temperature;
    private Double humidity;
    private Long zoneId;
    private LocalDateTime timestamp;
}
