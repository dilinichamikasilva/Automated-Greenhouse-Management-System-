package lk.ijse.sensorservice.controller;

import lk.ijse.sensorservice.service.TelemetryScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    @Autowired
    private TelemetryScheduler telemetryScheduler;

    @GetMapping("/latest")
    public Map<Long, Map<String, Object>> getLatest() {
        return telemetryScheduler.getLatestReadings();
    }
}