package db.team12.medical.repository;

import db.team12.medical.domain.PrescriptionMedicine;
import db.team12.medical.domain.PrescriptionMedicineId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionMedicineRepository
        extends JpaRepository<PrescriptionMedicine, PrescriptionMedicineId> {}
