package db.team12.medical.repository;

import db.team12.medical.domain.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymptomRepository extends JpaRepository<Symptom, Long> {}
