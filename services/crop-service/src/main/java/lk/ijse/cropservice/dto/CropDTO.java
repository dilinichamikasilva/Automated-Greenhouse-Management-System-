package lk.ijse.cropservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CropDTO implements Serializable {
    private String id;

    private String batchId;
    private String cropName;
    private String category;
    private String status;
    private Long zoneId;
    private LocalDate plantedDate;
}