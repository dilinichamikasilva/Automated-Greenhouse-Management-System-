package lk.ijse.zoneservice.service.impl;

import lk.ijse.zoneservice.dto.ZoneDTO;
import lk.ijse.zoneservice.entity.Zone;
import lk.ijse.zoneservice.exception.ResourceNotFoundException;
import lk.ijse.zoneservice.repository.ZoneRepository;
import lk.ijse.zoneservice.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;

    @Override
    public ZoneDTO createZone(ZoneDTO zoneDTO) {
        Zone zone = new Zone();
        BeanUtils.copyProperties(zoneDTO, zone);
        Zone savedZone = zoneRepository.save(zone);

        ZoneDTO responseDTO = new ZoneDTO();
        BeanUtils.copyProperties(savedZone, responseDTO);
        return responseDTO;
    }

    @Override
    public @Nullable List<ZoneDTO> getAllZones() {
        return zoneRepository.findAll().stream().map(zone -> {
            ZoneDTO dto = new ZoneDTO();
            BeanUtils.copyProperties(zone, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public @Nullable ZoneDTO getZoneById(Long id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + id));
        ZoneDTO dto = new ZoneDTO();
        BeanUtils.copyProperties(zone, dto);
        return dto;
    }
}
