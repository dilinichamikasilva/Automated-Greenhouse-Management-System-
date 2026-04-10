package lk.ijse.cropservice.service;

import lk.ijse.cropservice.dto.CropDTO;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface CropService {
    CropDTO saveCrop(CropDTO cropDTO);

    void updateStatus(String id, String status);

    @Nullable CropDTO getCropById(String id);

    @Nullable List<CropDTO> getAllCrops();
}
