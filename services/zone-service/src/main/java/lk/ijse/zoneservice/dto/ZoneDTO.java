package lk.ijse.zoneservice.dto;

import lombok.Data;

@Data
public class ZoneDTO {
    private Long id;
    private String name;
    private String description;
    private Double tempThreshold;
    private Double humidityThreshold;
}
