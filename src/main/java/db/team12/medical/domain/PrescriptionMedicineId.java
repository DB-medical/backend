package db.team12.medical.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PrescriptionMedicineId implements Serializable {

    @Column(name = "pres_id")
    private Long prescriptionId;

    @Column(name = "mid")
    private Long medicineId;
}
