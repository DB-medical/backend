package db.team12.medical.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "pharmacist")
public class Pharmacist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phid")
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharm_id")
    private Pharmacy pharmacy;
}
