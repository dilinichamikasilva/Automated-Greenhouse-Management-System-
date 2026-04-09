package lk.ijse.sensorservice.service;

import lk.ijse.sensorservice.dto.SensorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TelemetryScheduler {

    private final RestTemplate restTemplate;
    private final RestTemplate loadBalancedRestTemplate;

    private String cachedToken;
    private SensorDTO lastReading;

    @Value("${iot.external.auth-url}")
    private String authUrl;

    @Value("${iot.external.data-url}")
    private String dataUrl;

    @Value("${iot.external.username}")
    private String username;

    @Value("${iot.external.password}")
    private String password;

    private final String AUTOMATION_SERVICE_URL = "http://automation-service/api/v1/automation/process";

    public TelemetryScheduler(
            RestTemplate restTemplate,
            @Qualifier("loadBalancedRestTemplate") RestTemplate loadBalancedRestTemplate) {
        this.restTemplate = restTemplate;
        this.loadBalancedRestTemplate = loadBalancedRestTemplate;
    }

    private void refreshToken() {
        try {
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("email", username);
            loginRequest.put("password", password);

            ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, loginRequest, Map.class);
            if (response.getBody() != null) {
                this.cachedToken = (String) response.getBody().get("token");
                log.info("Token refreshed successfully.");
            }
        } catch (Exception e) {
            log.error("Failed to refresh token: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 10000)
    public void fetchAndPushTelemetry() {
        if (cachedToken == null) {
            refreshToken();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(cachedToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<SensorDTO> response = restTemplate.exchange(
                    dataUrl, HttpMethod.GET, entity, SensorDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                processAndPush(response.getBody());
            }

        } catch (HttpClientErrorException.Unauthorized e) {
            this.cachedToken = null;
        } catch (Exception e) {
            SensorDTO mockData = new SensorDTO(28.5, 72.0, 1L, LocalDateTime.now());
            processAndPush(mockData);
        }
    }

    private void processAndPush(SensorDTO data) {
        data.setTimestamp(LocalDateTime.now());
        this.lastReading = data;
        log.info("Pushing data to Automation Service: {}°C", data.getTemperature());

        try {
            loadBalancedRestTemplate.postForEntity(AUTOMATION_SERVICE_URL, data, Void.class);
        } catch (Exception e) {
            log.error("Could not push to Automation Service: {}", e.getMessage());
        }
    }

    public SensorDTO getLatestReading() {
        return lastReading;
    }
}