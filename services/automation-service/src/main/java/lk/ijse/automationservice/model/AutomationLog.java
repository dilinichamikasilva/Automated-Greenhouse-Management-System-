package lk.ijse.automationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "automation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutomationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long zoneId;
    private String action;
    private Double temperature;
    private Double humidity;
    private LocalDateTime triggeredAt;

    @PrePersist
    protected void onCreate() {
        triggeredAt = LocalDateTime.now();
    }


}
