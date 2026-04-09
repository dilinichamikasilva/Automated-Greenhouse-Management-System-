package lk.ijse.zoneservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ZoneResponse {
    private Long id;
    private String name;
    private Double minTemp;
    private Double maxTemp;
    private String deviceId;
    private LocalDateTime createdAt;
}