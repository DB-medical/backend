package db.team12.medical.service;

import db.team12.medical.domain.Pharmacy;
import db.team12.medical.dto.PharmacySearchResponse;
import db.team12.medical.repository.PharmacyRepository;
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

    public List<PharmacySearchResponse> search(String keyword, Integer size) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해 주세요.");
        }
        int limit = (size == null || size <= 0) ? DEFAULT_LIMIT : Math.min(size, 50);
        List<Pharmacy> pharmacies =
                pharmacyRepository.searchByKeyword(keyword.trim(), PageRequest.of(0, limit));

        return pharmacies.stream()
                .map(pharmacy -> PharmacySearchResponse.builder()
                        .id(pharmacy.getId())
                        .name(pharmacy.getName())
                        .address(pharmacy.getAddress())
                        .phone(pharmacy.getPhone())
                        .build())
                .toList();
    }
}
