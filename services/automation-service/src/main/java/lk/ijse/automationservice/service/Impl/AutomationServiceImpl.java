package lk.ijse.automationservice.service.Impl;

import lk.ijse.automationservice.client.ZoneClient;
import lk.ijse.automationservice.model.AutomationLog;
import lk.ijse.automationservice.repository.AutomationLogRepository;
import lk.ijse.automationservice.service.AutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AutomationServiceImpl implements AutomationService {

    private AutomationLogRepository logRepository;
    private ZoneClient zoneClient;

    @Override
    public void processTelemetry(Map<String, Object> telemetry) {
        Long zoneId = ((Number) telemetry.get("zoneId")).longValue();
        Double currentTemp = ((Number) telemetry.get("temperature")).doubleValue();

        Map<String, Object> zone = zoneClient.getZoneById(zoneId);
        Double minTemp = ((Number) zone.get("minTemp")).doubleValue();
        Double maxTemp = ((Number) zone.get("maxTemp")).doubleValue();

        String action = "STATUS_NORMAL";
        if (currentTemp > maxTemp) {
            action = "TURN_FAN_ON";
        } else if (currentTemp < minTemp) {
            action = "TURN_HEATER_ON";
        }

        AutomationLog log = new AutomationLog();
        log.setZoneId(zoneId);
        log.setTemperature(currentTemp);
        log.setAction(action);

        logRepository.save(log);
        System.out.println("Processed automation for zone " + zoneId + ": " + action);
    }

    @Override
    public List<AutomationLog> getAllLogs() {
        return logRepository.findAllByOrderByTriggeredAtDesc();
    }
}
