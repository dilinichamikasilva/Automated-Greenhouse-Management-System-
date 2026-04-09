package lk.ijse.zoneservice.controller;


import lk.ijse.zoneservice.dto.ZoneRequest;
import lk.ijse.zoneservice.dto.ZoneResponse;
import lk.ijse.zoneservice.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ZoneResponse createZone(@RequestBody ZoneRequest request) {
        return zoneService.createZone(request);
    }

    @GetMapping("/get-all")
    public List<ZoneResponse> getAllZones() {
        return zoneService.getAllZones();
    }

    @GetMapping("/{id}")
    public ZoneResponse getZoneById(@PathVariable Long id) {
        return zoneService.getZoneById(id);
    }

    @PutMapping("/{id}")
    public ZoneResponse updateZone(@PathVariable Long id, @RequestBody ZoneRequest request) {
        return zoneService.updateZone(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
    }
}
