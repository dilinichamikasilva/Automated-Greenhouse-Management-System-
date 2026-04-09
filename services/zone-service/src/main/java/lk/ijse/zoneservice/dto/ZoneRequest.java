package lk.ijse.zoneservice.dto;

import lombok.Data;

@Data
public class ZoneRequest {
    private String name;
    private Double minTemp;
    private Double maxTemp;
}