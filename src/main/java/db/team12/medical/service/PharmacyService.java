package db.team12.medical.service;

import db.team12.medical.domain.Doctor;
import db.team12.medical.domain.Pharmacy;
import db.team12.medical.dto.PharmacySearchResponse;
import db.team12.medical.repository.DoctorRepository;
import db.team12.medical.repository.PharmacyRepository;
import db.team12.medical.security.MemberPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PharmacyService {

    private static final int DEFAULT_LIMIT = 10;

    private final PharmacyRepository pharmacyRepository;
    private final DoctorRepository doctorRepository;

    public List<PharmacySearchResponse> search(MemberPrincipal principal, String keyword, Integer size) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해 주세요.");
        }
        Doctor doctor = doctorRepository
                .findByMemberId(principal.getId())
                .orElseThrow(() -> new IllegalStateException("의사 계정만 약국을 검색할 수 있습니다."));
        if (doctor.getHospital() == null) {
            throw new IllegalStateException("소속 병원이 등록되지 않은 의사 계정입니다.");
        }
        Long hospitalId = doctor.getHospital().getId();
        int limit = (size == null || size <= 0) ? DEFAULT_LIMIT : Math.min(size, 50);
        List<Pharmacy> pharmacies =
                pharmacyRepository.searchByKeywordAndHospital(hospitalId, keyword.trim(), PageRequest.of(0, limit));

        return pharmacies.stream()
                .map(pharmacy -> PharmacySearchResponse.builder()
                        .id(pharmacy.getId())
                        .name(pharmacy.getName())
                        .address(pharmacy.getAddress())
                        .phone(pharmacy.getPhone())
                        .hospitalId(
                                pharmacy.getHospital() != null ? pharmacy.getHospital().getId() : null)
                        .hospitalName(
                                pharmacy.getHospital() != null ? pharmacy.getHospital().getName() : null)
                        .build())
                .toList();
    }
}
