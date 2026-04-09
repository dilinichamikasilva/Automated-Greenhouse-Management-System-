package lk.ijse.sensorservice.controller;

import lk.ijse.sensorservice.dto.SensorDTO;
import lk.ijse.sensorservice.service.TelemetryScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final TelemetryScheduler telemetryScheduler;

    @GetMapping("/latest")
    public ResponseEntity<SensorDTO> getLatest() {
        SensorDTO latest = telemetryScheduler.getLatestReading();
        return latest != null ? ResponseEntity.ok(latest) : ResponseEntity.noContent().build();
    }
}
