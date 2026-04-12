package lk.ijse.automationservice.service.Impl;

import lk.ijse.automationservice.client.ZoneClient;
import lk.ijse.automationservice.model.AutomationLog;
import lk.ijse.automationservice.repository.AutomationLogRepository;
import lk.ijse.automationservice.service.AutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AutomationServiceImpl implements  AutomationService{

    @Autowired
    private  AutomationLogRepository automationLogRepository;
    @Autowired
    private  ZoneClient zoneClient;

    public void processTelemetry(Map<String, Object> telemetry) {
        Long zoneId = ((Number) telemetry.get("zoneId")).longValue();

        Double currentTemp;
        Double currentHumidity = null;

        if (telemetry.containsKey("value") && telemetry.get("value") instanceof Map) {
            Map<String, Object> valueMap = (Map<String, Object>) telemetry.get("value");
            currentTemp = ((Number) valueMap.get("temperature")).doubleValue();
            if (valueMap.containsKey("humidity")) {
                currentHumidity = ((Number) valueMap.get("humidity")).doubleValue();
            }
        } else {
            currentTemp = ((Number) telemetry.get("temperature")).doubleValue();
            if (telemetry.containsKey("humidity")) {
                currentHumidity = ((Number) telemetry.get("humidity")).doubleValue();
            }
        }

        Map<String, Object> zone = zoneClient.getZoneById(zoneId);
        Double minTemp = ((Number) zone.get("minTemp")).doubleValue();
        Double maxTemp = ((Number) zone.get("maxTemp")).doubleValue();

        String action = "STATUS_NORMAL";
        if (currentTemp > maxTemp) {
            action = "TURN_FAN_ON";
        } else if (currentTemp < minTemp) {
            action = "TURN_HEATER_ON";
        }

        AutomationLog automationLog = new AutomationLog();
        automationLog.setZoneId(zoneId);
        automationLog.setTemperature(currentTemp);
        automationLog.setHumidity(currentHumidity);
        automationLog.setAction(action);

        automationLogRepository.save(automationLog);
        System.out.println("Rule applied for zone " + zoneId + ": " + action);
    }

    public List<AutomationLog> getAllLogs() {
        return automationLogRepository.findAllByOrderByTriggeredAtDesc();
    }

}
