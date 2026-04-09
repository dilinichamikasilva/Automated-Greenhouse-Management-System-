package lk.ijse.sensorservice.service;

import lk.ijse.sensorservice.client.AutomationClient;
import lk.ijse.sensorservice.client.ZoneClient;
import lk.ijse.sensorservice.manager.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class TelemetryScheduler {

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private ZoneClient zoneClient;

    @Autowired
    private AutomationClient automationClient;

    @Value("${iot.api.base-url}")
    private String iotBaseUrl;

    private final WebClient webClient;

    public TelemetryScheduler(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Scheduled(fixedDelay = 10000)
    public void fetchAndPush() {
        System.out.println("Fetching telemetry...");
        try {
            List<Map<String, Object>> zones = zoneClient.getAllZones();
            for (Map<String, Object> zone : zones) {
                String deviceId = (String) zone.get("deviceId");
                Long zoneId = ((Number) zone.get("id")).longValue();

                if (deviceId != null) {
                    fetchAndPushForDevice(deviceId, zoneId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching zones: " + e.getMessage());
        }
    }

    private void fetchAndPushForDevice(String deviceId, Long zoneId) {
        try {
            Map telemetry = webClient.get()
                    .uri(iotBaseUrl + "/devices/telemetry/" + deviceId)
                    .header("Authorization", "Bearer " + tokenManager.getAccessToken())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (telemetry != null) {
                telemetry.put("zoneId", zoneId);
                telemetry.put("deviceId", deviceId);
                automationClient.process(telemetry);
                System.out.println("Pushed telemetry for device: " + deviceId);
            }
        } catch (WebClientResponseException.Unauthorized e) {
            tokenManager.refresh();
        } catch (Exception e) {
            System.err.println("Error fetching telemetry for device " + deviceId + ": " + e.getMessage());
        }
    }
}
