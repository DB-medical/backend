package db.team12.medical.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pid")
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "ssn", length = 14, nullable = false, unique = true)
    private String ssn;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "history", columnDefinition = "TEXT")
    private String history;

    @Builder.Default
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRecord> medicalRecords = new ArrayList<>();
}
