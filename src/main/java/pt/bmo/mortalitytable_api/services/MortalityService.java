package pt.bmo.mortalitytable_api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import pt.bmo.mortalitytable_api.domain.Mortality;

import java.util.Optional;
import java.util.UUID;

public interface MortalityService {
    Page<Mortality> getAll(Pageable pageable);
    Page<Mortality> getByYear(Pageable pageable, int year);
    Optional<Mortality> create(Mortality mortality);
    Optional<Mortality> update(UUID id, Mortality mortality);
    void saveRecords(MultipartFile file);
}
