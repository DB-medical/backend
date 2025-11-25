package db.team12.medical.repository;

import db.team12.medical.domain.Medicine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findTop20ByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
