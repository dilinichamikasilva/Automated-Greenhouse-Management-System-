package lk.ijse.cropservice.service.impl;

import lk.ijse.cropservice.dto.CropDTO;
import lk.ijse.cropservice.model.Crop;
import lk.ijse.cropservice.repository.CropRepository;
import lk.ijse.cropservice.service.CropService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;
    private final ModelMapper modelMapper;

    @Override
    public CropDTO saveCrop(CropDTO cropDTO) {
        Crop crop = modelMapper.map(cropDTO, Crop.class);
        crop.setStatus("SEEDLING");
        crop.setPlantedDate(LocalDate.now());
        return modelMapper.map(cropRepository.save(crop), CropDTO.class);
    }

    @Override
    public void updateStatus(String id, String status) {
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop Batch not found: " + id));

        String currentStatus = crop.getStatus().toUpperCase();
        String targetStatus = status.toUpperCase();

        if (isValidTransition(currentStatus, targetStatus)) {
            crop.setStatus(targetStatus);
            cropRepository.save(crop);
        } else {
            throw new IllegalArgumentException("Invalid state transition from " + currentStatus + " to " + targetStatus);
        }
    }

    private boolean isValidTransition(String currentStatus, String targetStatus) {
        switch (currentStatus) {
            case "SEEDLING":
                return targetStatus.equals("VEGETATIVE");
            case "VEGETATIVE":
                return targetStatus.equals("HARVESTED");
            default:
                return false;
        }
    }

    @Override
    public @Nullable CropDTO getCropById(String id) {
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));
        return modelMapper.map(crop, CropDTO.class);
    }

    @Override
    public @Nullable List<CropDTO> getAllCrops() {
        return cropRepository.findAll().stream()
                .map(crop -> modelMapper.map(crop, CropDTO.class))
                .collect(Collectors.toList());
    }
}
