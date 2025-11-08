package db.team12.medical.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "medicine")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mid")
    private Long id;

    @Column(name = "mname", length = 100, nullable = false)
    private String name;

    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    @Column(name = "efficacy", columnDefinition = "TEXT")
    private String efficacy;

    @Builder.Default
    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PrescriptionMedicine> prescriptions = new HashSet<>();
}
