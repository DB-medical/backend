package db.team12.medical.repository;

import db.team12.medical.domain.Pharmacist;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacistRepository extends JpaRepository<Pharmacist, Long> {

    Optional<Pharmacist> findByMemberId(Long memberId);
}
