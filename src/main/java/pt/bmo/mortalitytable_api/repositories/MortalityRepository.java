package pt.bmo.mortalitytable_api.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pt.bmo.mortalitytable_api.domain.Mortality;

import java.util.UUID;

@Repository
public interface MortalityRepository extends PagingAndSortingRepository<Mortality, UUID>, JpaRepository<Mortality, UUID> {
    Page<Mortality> findByYear(Pageable pageable, int year);
    void deleteByCountryAndYear(String country, int year);
}
