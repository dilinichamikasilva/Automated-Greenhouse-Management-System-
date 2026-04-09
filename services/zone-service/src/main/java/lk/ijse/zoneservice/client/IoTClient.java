package lk.ijse.zoneservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "iot-client", url = "http://104.211.95.241:8080/api")
public interface IoTClient {

    @PostMapping("/devices")
    Map<String, Object> registerDevice(@RequestBody Map<String, Object> deviceRequest);
}
