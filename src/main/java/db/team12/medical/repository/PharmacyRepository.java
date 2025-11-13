package db.team12.medical.repository;

import db.team12.medical.domain.Pharmacy;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    @Query("""
            select p from Pharmacy p
            where lower(p.name) like lower(concat('%', :keyword, '%'))
               or lower(p.address) like lower(concat('%', :keyword, '%'))
            order by p.name asc
            """)
    List<Pharmacy> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
