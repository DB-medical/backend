package db.team12.medical.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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
@Table(name = "symptom")
public class Symptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sid")
    private Long id;

    @Column(name = "sname", length = 100, nullable = false)
    private String name;

    @Column(name = "body_part", length = 50)
    private String bodyPart;

    @Builder.Default
    @ManyToMany(mappedBy = "symptoms")
    private Set<MedicalRecord> medicalRecords = new HashSet<>();
}
