package lk.ijse.zoneservice.controller;

import lk.ijse.zoneservice.dto.ZoneDTO;
import lk.ijse.zoneservice.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {


    private final ZoneService zoneService;

    @PostMapping("/create")
    public ResponseEntity<ZoneDTO> createZone(@RequestBody ZoneDTO zoneDTO) {
        return new ResponseEntity<>(zoneService.createZone(zoneDTO), HttpStatus.CREATED);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ZoneDTO>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getZoneById(@PathVariable Long id) {
        return ResponseEntity.ok(zoneService.getZoneById(id));
    }
}
