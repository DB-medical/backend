package db.team12.medical.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "prescription")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pres_id")
    private Long id;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "status", length = 20)
    private PrescriptionStatus status;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rid", unique = true)
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharm_id")
    private Pharmacy pharmacy;

    @Builder.Default
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PrescriptionMedicine> medicines = new HashSet<>();
}
