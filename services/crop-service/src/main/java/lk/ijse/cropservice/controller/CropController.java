package lk.ijse.cropservice.controller;

import lk.ijse.cropservice.dto.CropDTO;
import lk.ijse.cropservice.service.CropService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crops")
@RequiredArgsConstructor
public class CropController {

    private final CropService cropService;

    @PostMapping
    public ResponseEntity<CropDTO> registerBatch(@RequestBody CropDTO cropDTO) {
        CropDTO savedBatch = cropService.saveCrop(cropDTO);
        return new ResponseEntity<>(savedBatch, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateLifecycle(
            @PathVariable String id,
            @RequestParam String status) {

        cropService.updateStatus(id, status);
        return ResponseEntity.ok("Crop status updated successfully to: " + status.toUpperCase());
    }

    @GetMapping
    public ResponseEntity<List<CropDTO>> getInventory() {
        return ResponseEntity.ok(cropService.getAllCrops());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CropDTO> getCropById(@PathVariable String id) {
        return ResponseEntity.ok(cropService.getCropById(id));
    }
}
