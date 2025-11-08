package db.team12.medical.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PrescriptionStatusConverter implements AttributeConverter<PrescriptionStatus, String> {

    @Override
    public String convertToDatabaseColumn(PrescriptionStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public PrescriptionStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        for (PrescriptionStatus status : PrescriptionStatus.values()) {
            if (status.getValue().equals(dbData)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown prescription status: " + dbData);
    }
}
