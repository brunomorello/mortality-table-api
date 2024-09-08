package pt.bmo.mortalitytable_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pt.bmo.mortalitytable_api.controllers.dto.MortalityDto;
import pt.bmo.mortalitytable_api.domain.Mortality;
import pt.bmo.mortalitytable_api.domain.mapper.MortalityMapper;
import pt.bmo.mortalitytable_api.exception.NotFoundException;
import pt.bmo.mortalitytable_api.externalservice.PopulationStatisticService;
import pt.bmo.mortalitytable_api.externalservice.model.PopulationStatisticDto;
import pt.bmo.mortalitytable_api.repositories.MortalityRepository;
import pt.bmo.mortalitytable_api.utils.CsvHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MortalityServiceImpl implements MortalityService {

    private final MortalityRepository mortalityRepository;
    private final PopulationStatisticService populationStatisticService;

    @Override
    public Page<Mortality> getAll(Pageable pageable) {
        return mortalityRepository.findAll(pageable);
    }

    @Override
    public Page<Mortality> getByYear(Pageable pageable, int year) {
        return mortalityRepository.findByYear(pageable, year);
    }

    @Override
    public Optional<Mortality> create(Mortality mortality) {
        validateCountry(mortality.getCountry());

        PopulationStatisticDto statistics = populationStatisticService.findByCountryAndYear(mortality.getCountry(), mortality.getYear());

        mortality.setFemalePopulation(statistics.femalePopulation());
        mortality.setMalePopulation(statistics.malePopulation());

        return Optional.of(mortalityRepository.save(mortality));
    }

    @Override
    public Optional<Mortality> update(UUID id, Mortality mortality) {
        Mortality recordFound = mortalityRepository.findById(id).orElseThrow(() -> new NotFoundException("Record not found for giving id"));

        validateCountry(mortality.getCountry());
        BeanUtils.copyProperties(mortality, recordFound);
        return Optional.of(mortalityRepository.save(recordFound));
    }

    private void validateCountry(final String countryCode) {
        if (!Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2).contains(countryCode)) {
            throw new IllegalArgumentException(String.format("Invalid country code: %s", countryCode));
        }
    }

    @Override
    public void saveRecords(MultipartFile file) {
        try {
            List<MortalityDto> mortalityDtos = CsvHelper.csvToMortalityDtoList(file.getInputStream());
            mortalityDtos.forEach(mortalityDto -> {
                validateCountry(mortalityDto.country());
                mortalityRepository.deleteByCountryAndYear(mortalityDto.country(), mortalityDto.year());
                mortalityRepository.save(MortalityMapper.INSTANCE.toModel(mortalityDto));
            });
        } catch (IOException e) {
            throw new RuntimeException("Fail to parse and store batch data");
        }
    }
}
