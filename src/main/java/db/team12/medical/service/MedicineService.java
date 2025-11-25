package db.team12.medical.service;

import db.team12.medical.domain.Doctor;
import db.team12.medical.domain.Medicine;
import db.team12.medical.dto.MedicineSummaryResponse;
import db.team12.medical.repository.DoctorRepository;
import db.team12.medical.repository.MedicineRepository;
import db.team12.medical.security.MemberPrincipal;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final DoctorRepository doctorRepository;

    public List<MedicineSummaryResponse> searchMedicines(MemberPrincipal principal, String keyword) {
        verifyDoctor(principal);
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        return medicineRepository
                .findTop20ByNameContainingIgnoreCaseOrderByNameAsc(keyword.trim())
                .stream()
                .map(this::toSummary)
                .toList();
    }

    private void verifyDoctor(MemberPrincipal principal) {
        doctorRepository
                .findByMemberId(principal.getId())
                .orElseThrow(() -> new IllegalStateException("의사 계정만 의약품을 검색할 수 있습니다."));
    }

    private MedicineSummaryResponse toSummary(Medicine medicine) {
        return MedicineSummaryResponse.builder()
                .id(medicine.getId())
                .name(medicine.getName())
                .manufacturer(medicine.getManufacturer())
                .efficacy(medicine.getEfficacy())
                .build();
    }
}
