package lk.ijse.sensorservice.service;

import lk.ijse.sensorservice.client.AutomationClient;
import lk.ijse.sensorservice.client.ZoneClient;
import lk.ijse.sensorservice.manager.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetryScheduler {

    private final TokenManager tokenManager;
    private final ZoneClient zoneClient;
    private final AutomationClient automationClient;
    private final WebClient webClient;

    @Value("${iot.api.base-url}")
    private String iotBaseUrl;

    private final Map<Long, Map<String, Object>> latestReadings = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 10000)
    public void fetchAndPush() {
        log.info("Starting telemetry fetch cycle...");
        try {
            List<Map<String, Object>> zones = zoneClient.getAllZones();
            if (zones == null || zones.isEmpty()) {
                log.warn("No zones found to monitor.");
                return;
            }

            for (Map<String, Object> zone : zones) {
                Object deviceIdObj = zone.get("deviceId");
                Object idObj = zone.get("id");

                if (deviceIdObj != null && idObj != null) {
                    String deviceId = deviceIdObj.toString();
                    Long zoneId = ((Number) idObj).longValue();
                    fetchWithRetry(deviceId, zoneId, true);
                }
            }
        } catch (Exception e) {
            log.error("Critical error during zone discovery: {}", e.getMessage());
        }
    }

    private void fetchWithRetry(String deviceId, Long zoneId, boolean canRetry) {
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

                latestReadings.put(zoneId, telemetry);

                automationClient.process(telemetry);
                System.out.println("Pushed telemetry for device: " + deviceId);
            }
        } catch (WebClientResponseException.Unauthorized e) {
            if (canRetry) {
                System.out.println("Token expired. Refreshing and retrying...");
                tokenManager.refresh();
                fetchWithRetry(deviceId, zoneId, false);
            } else {
                tokenManager.login();
            }
        } catch (Exception e) {
            System.err.println("Error fetching telemetry for device " + deviceId + ": " + e.getMessage());
        }
    }

    public Map<Long, Map<String, Object>> getLatestReadings() {
        return latestReadings;
    }
}