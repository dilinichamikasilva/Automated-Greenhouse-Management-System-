package lk.ijse.sensorservice.manager;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class TokenManager {

    @Value("${iot.api.base-url}")
    private String baseUrl;

    @Value("${iot.credentials.username}")
    private String username;

    @Value("${iot.credentials.password}")
    private String password;

    private String accessToken;
    private String refreshToken;

    private final WebClient webClient;

    public TokenManager(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @PostConstruct
    public void init() {
        login();
    }

    public synchronized void login() {
        try {
            Map<String, String> response = webClient.post()
                    .uri(baseUrl + "/auth/login")
                    .bodyValue(Map.of("username", username, "password", password))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                this.accessToken = response.get("accessToken");
                this.refreshToken = response.get("refreshToken");
            }
        } catch (Exception e) {
            System.err.println("Failed to login to IoT API: " + e.getMessage());
        }
    }

    public synchronized String getAccessToken() {
        return accessToken;
    }

    public synchronized void refresh() {
        try {
            Map<String, String> response = webClient.post()
                    .uri(baseUrl + "/auth/refresh")
                    .bodyValue(Map.of("refreshToken", refreshToken))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                this.accessToken = response.get("accessToken");
            }
        } catch (Exception e) {
            System.err.println("Failed to refresh IoT token: " + e.getMessage());
            login();
        }
    }
}

