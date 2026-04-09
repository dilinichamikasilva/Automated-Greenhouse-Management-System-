package lk.ijse.zoneservice.service.impl;

import lk.ijse.zoneservice.client.IoTClient;
import lk.ijse.zoneservice.dto.ZoneRequest;
import lk.ijse.zoneservice.dto.ZoneResponse;
import lk.ijse.zoneservice.model.Zone;
import lk.ijse.zoneservice.repository.ZoneRepository;
import lk.ijse.zoneservice.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final IoTClient iotClient;

    @Override
    public ZoneResponse createZone(ZoneRequest request) {
        if (request.getMinTemp() >= request.getMaxTemp()) {
            throw new IllegalArgumentException("Min temperature must be less than max temperature.");
        }

        String deviceName = "ZoneDevice-" + request.getName();
        String deviceId;
        try {
            Map<String, Object> iotResponse = iotClient.registerDevice(Map.of("name", deviceName));
            deviceId = iotResponse.get("id").toString();
        } catch (Exception e) {
            deviceId = UUID.randomUUID().toString();
        }

        Zone zone = new Zone();
        zone.setName(request.getName());
        zone.setMinTemp(request.getMinTemp());
        zone.setMaxTemp(request.getMaxTemp());
        zone.setDeviceId(deviceId);

        Zone savedZone = zoneRepository.save(zone);
        return mapToResponse(savedZone);
    }

    private ZoneResponse mapToResponse(Zone zone) {
        return ZoneResponse.builder()
                .id(zone.getId())
                .name(zone.getName())
                .minTemp(zone.getMinTemp())
                .maxTemp(zone.getMaxTemp())
                .deviceId(zone.getDeviceId())
                .createdAt(zone.getCreatedAt())
                .build();
    }

    @Override
    public List<ZoneResponse> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ZoneResponse getZoneById(Long id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + id));
        return mapToResponse(zone);
    }

    @Override
    public ZoneResponse updateZone(Long id, ZoneRequest request) {
        if (request.getMinTemp() >= request.getMaxTemp()) {
            throw new IllegalArgumentException("Min temperature must be less than max temperature.");
        }

        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + id));

        zone.setName(request.getName());
        zone.setMinTemp(request.getMinTemp());
        zone.setMaxTemp(request.getMaxTemp());

        Zone updatedZone = zoneRepository.save(zone);
        return mapToResponse(updatedZone);
    }

    @Override
    public void deleteZone(Long id) {
        zoneRepository.deleteById(id);
    }
}
