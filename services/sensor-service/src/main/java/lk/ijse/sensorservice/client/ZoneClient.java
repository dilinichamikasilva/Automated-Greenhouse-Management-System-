package lk.ijse.sensorservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "zone-service")
public interface ZoneClient {

    @GetMapping("/api/zones")
    List<Map<String, Object>> getAllZones();
}