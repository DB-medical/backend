package db.team12.medical.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import db.team12.medical.domain.Pharmacy;
import db.team12.medical.dto.PharmacySearchResponse;
import db.team12.medical.repository.PharmacyRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PharmacyServiceTest {

    @Autowired
    private PharmacyService pharmacyService;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @BeforeEach
    void setUp() {
        pharmacyRepository.save(Pharmacy.builder()
                .name("한빛온누리약국")
                .address("서울시 성동구 왕십리로")
                .phone("02-100-2000")
                .build());
        pharmacyRepository.save(Pharmacy.builder()
                .name("왕십리중앙약국")
                .address("서울시 성동구 무학로")
                .phone("02-300-4000")
                .build());
    }

    @Test
    @DisplayName("키워드로 약국을 검색한다")
    void search_returnsMatchedPharmacies() {
        List<PharmacySearchResponse> responses = pharmacyService.search("왕십리", 5);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).contains("약국");
    }

    @Test
    @DisplayName("검색어 없이 조회할 수 없다")
    void search_requiresKeyword() {
        assertThatThrownBy(() -> pharmacyService.search(" ", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("검색어");
    }
}
