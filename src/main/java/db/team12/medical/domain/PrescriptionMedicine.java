package db.team12.medical.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "prescription_medicine")
public class PrescriptionMedicine {

    @EmbeddedId
    private PrescriptionMedicineId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("prescriptionId")
    @JoinColumn(name = "pres_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("medicineId")
    @JoinColumn(name = "mid", nullable = false)
    private Medicine medicine;

    @Column(name = "dosage", length = 50)
    private String dosage;

    @Column(name = "frequency", length = 50)
    private String frequency;

    @Column(name = "days")
    private Integer days;

    public PrescriptionMedicine(
            Prescription prescription, Medicine medicine, String dosage, String frequency, Integer days) {
        this.prescription = prescription;
        this.medicine = medicine;
        this.dosage = dosage;
        this.frequency = frequency;
        this.days = days;
        this.id = new PrescriptionMedicineId(
                prescription != null ? prescription.getId() : null,
                medicine != null ? medicine.getId() : null);
    }
}
