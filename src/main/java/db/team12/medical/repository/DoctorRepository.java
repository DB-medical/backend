package db.team12.medical.repository;

import db.team12.medical.domain.Doctor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByMemberId(Long memberId);
}
