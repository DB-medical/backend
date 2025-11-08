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
@Table(name = "hospital")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hid")
    private Long id;

    @Column(name = "hname", length = 100, nullable = false)
    private String name;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Builder.Default
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Department> departments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "hospital")
    private List<Doctor> doctors = new ArrayList<>();
}
