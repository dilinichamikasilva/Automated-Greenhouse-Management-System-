package lk.ijse.automationservice.service;

import lk.ijse.automationservice.model.AutomationLog;

import java.util.List;
import java.util.Map;

public interface AutomationService {
    void processTelemetry(Map<String, Object> telemetry);

    List<AutomationLog> getAllLogs();
}
