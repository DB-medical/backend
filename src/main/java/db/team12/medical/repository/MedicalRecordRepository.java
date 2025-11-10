package db.team12.medical.repository;

import db.team12.medical.domain.MedicalRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findAllByOrderByVisitDateDesc();

    List<MedicalRecord> findAllByPatientIdOrderByVisitDateDesc(Long patientId);
}
