package db.team12.medical.repository;

import db.team12.medical.domain.Prescription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @EntityGraph(attributePaths = {
        "medicalRecord",
        "medicalRecord.patient",
        "medicalRecord.doctor",
        "medicalRecord.doctor.member",
        "pharmacy"
    })
    List<Prescription> findAllByPharmacyIdOrderByIssueDateDesc(Long pharmacyId);

    @EntityGraph(attributePaths = {
        "medicalRecord",
        "medicalRecord.patient",
        "medicalRecord.doctor",
        "medicalRecord.doctor.member",
        "pharmacy"
    })
    List<Prescription> findAllByMedicalRecordDoctorIdOrderByIssueDateDesc(Long doctorId);

    @Query("""
            select distinct p from Prescription p
            join fetch p.medicalRecord mr
            join fetch mr.patient
            join fetch mr.doctor d
            join fetch d.member
            left join fetch p.pharmacy
            left join fetch p.medicines pm
            left join fetch pm.medicine m
            left join fetch m.ingredients
            where p.id = :id
            """)
    Optional<Prescription> findDetailById(@Param("id") Long id);
}
