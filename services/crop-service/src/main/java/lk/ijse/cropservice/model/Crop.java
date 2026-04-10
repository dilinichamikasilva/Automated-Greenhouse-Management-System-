package lk.ijse.cropservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "crops")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Crop {
    @Id
    private String id;
    private String batchId;
    private String cropName;
    private String category;
    private String status;
    private Long zoneId;
    private LocalDate plantedDate;
}
