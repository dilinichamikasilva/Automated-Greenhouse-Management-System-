package lk.ijse.zoneservice.service;

import lk.ijse.zoneservice.dto.ZoneRequest;
import lk.ijse.zoneservice.dto.ZoneResponse;

import java.util.List;

public interface ZoneService {
    ZoneResponse createZone(ZoneRequest request);

    List<ZoneResponse> getAllZones();

    ZoneResponse getZoneById(Long id);

    ZoneResponse updateZone(Long id, ZoneRequest request);

    void deleteZone(Long id);
}
