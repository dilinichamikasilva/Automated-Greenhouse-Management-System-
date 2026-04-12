package lk.ijse.automationservice.controller;

import lk.ijse.automationservice.model.AutomationLog;
import lk.ijse.automationservice.service.AutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/automation")
public class AutomationController {

    private final AutomationService automationService;

    @PostMapping("/process")
    public void process(@RequestBody Map<String, Object> telemetry) {
        automationService.processTelemetry(telemetry);
    }

    @GetMapping("/logs")
    public List<AutomationLog> getLogs() {
        return automationService.getAllLogs();
    }
}