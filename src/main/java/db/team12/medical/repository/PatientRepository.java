package db.team12.medical.repository;

import db.team12.medical.domain.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findBySsn(String ssn);

    List<Patient> findTop20ByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
