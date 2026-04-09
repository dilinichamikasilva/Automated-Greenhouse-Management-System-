package lk.ijse.zoneservice.service;

import lk.ijse.zoneservice.dto.ZoneDTO;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ZoneService {
    ZoneDTO createZone(ZoneDTO zoneDTO);

    @Nullable List<ZoneDTO> getAllZones();

    @Nullable ZoneDTO getZoneById(Long id);
}
