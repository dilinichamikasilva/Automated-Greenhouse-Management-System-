package lk.ijse.cropservice.repository;

import lk.ijse.cropservice.model.Crop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropRepository extends MongoRepository<Crop, String> {
    List<Crop> findByZoneId(Long zoneId);
}
