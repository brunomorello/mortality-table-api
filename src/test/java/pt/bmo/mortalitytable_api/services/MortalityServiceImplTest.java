package pt.bmo.mortalitytable_api.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import pt.bmo.mortalitytable_api.controllers.dto.MortalityDto;
import pt.bmo.mortalitytable_api.domain.Mortality;
import pt.bmo.mortalitytable_api.exception.NotFoundException;
import pt.bmo.mortalitytable_api.externalservice.PopulationStatisticService;
import pt.bmo.mortalitytable_api.externalservice.exception.ExternalSystemException;
import pt.bmo.mortalitytable_api.externalservice.model.PopulationStatisticDto;
import pt.bmo.mortalitytable_api.helper.MortalityTableApiHelper;
import pt.bmo.mortalitytable_api.repositories.MortalityRepository;
import pt.bmo.mortalitytable_api.utils.CsvHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MortalityServiceImplTest {

    @Mock
    private MortalityRepository mortalityRepository;

    @Mock
    private Pageable pageable;

    @Mock
    private PopulationStatisticService populationStatisticService;

    @Mock
    private MultipartFile file;

    @Mock
    private InputStream inputStream;

    @InjectMocks
    private MortalityServiceImpl service;

    @Captor
    private ArgumentCaptor<Mortality> mortalityArgumentCaptor;

    @Test
    @DisplayName("When getAll records the return a Page containing it")
    void getAll() {
        PageImpl<Mortality> mortalityPage = new PageImpl<>(Arrays.asList(MortalityTableApiHelper.createMortality()));
        when(mortalityRepository.findAll(pageable)).thenReturn(mortalityPage);

        Page<Mortality> mortalities = service.getAll(pageable);

        assertTrue(mortalities.hasContent());
    }

    @Test
    @DisplayName("When get record by year then return a Page containing it")
    void getByYear() {
        PageImpl<Mortality> mortalityPage = new PageImpl<>(Arrays.asList(MortalityTableApiHelper.createMortality()));
        final int currentYear = LocalDate.now().getYear();

        when(mortalityRepository.findByYear(pageable, currentYear)).thenReturn(mortalityPage);

        Page<Mortality> mortalities = service.getByYear(pageable, currentYear);

        assertTrue(mortalities.hasContent());
    }

    @Test
    @DisplayName("When get record by year and no results were found")
    void getByYearNotFound() {
        PageImpl<Mortality> mortalityPage = new PageImpl<>(Collections.emptyList());
        final int currentYear = LocalDate.now().getYear();

        when(mortalityRepository.findByYear(pageable, currentYear)).thenReturn(mortalityPage);

        Page<Mortality> mortalities = service.getByYear(pageable, currentYear);

        assertFalse(mortalities.hasContent());
    }

    @Test
    void create() {
        Mortality mortality = MortalityTableApiHelper.createMortality();
        mortality.setMaleTx(BigDecimal.valueOf(5.2));
        mortality.setFemaleTx(BigDecimal.valueOf(10.22));

        PopulationStatisticDto populationStatisticDto = new PopulationStatisticDto(mortality.getCountry(), mortality.getYear(), 10, 10);

        when(populationStatisticService.findByCountryAndYear(Mockito.anyString(), Mockito.anyInt())).thenReturn(populationStatisticDto);
        when(mortalityRepository.save(any(Mortality.class))).thenReturn(mortality);
        service.create(mortality);

        verify(mortalityRepository).save(mortalityArgumentCaptor.capture());
        Mortality mortalityCreated = mortalityArgumentCaptor.getValue();

        assertEquals(mortality, mortalityCreated);
    }

    @Test
    void createWithUnknownCountryCode() {
        Mortality mortality = MortalityTableApiHelper.createMortality();
        mortality.setCountry("TEST");
        mortality.setMaleTx(BigDecimal.valueOf(5.2));
        mortality.setFemaleTx(BigDecimal.valueOf(10.22));

        assertThrows(IllegalArgumentException.class, () -> service.create(mortality));
    }

    @Test
    void createFailsDueExternalSystem() {
        Mortality mortality = MortalityTableApiHelper.createMortality();
        mortality.setMaleTx(BigDecimal.valueOf(5.2));
        mortality.setFemaleTx(BigDecimal.valueOf(10.22));

        when(populationStatisticService.findByCountryAndYear(Mockito.anyString(), Mockito.anyInt())).thenThrow(new ExternalSystemException("error"));

        assertThrows(ExternalSystemException.class, () -> service.create(mortality));
    }

    @Test
    void update() {
        Mortality mortality = MortalityTableApiHelper.createMortality();
        mortality.setMaleTx(BigDecimal.valueOf(5.2));
        mortality.setFemaleTx(BigDecimal.valueOf(10.22));
        mortality.setCountry("BR");

        when(mortalityRepository.findById(any(UUID.class))).thenReturn(Optional.of(mortality));
        when(mortalityRepository.save(any(Mortality.class))).thenReturn(mortality);
        service.update(mortality.getId(), mortality);

        verify(mortalityRepository).save(mortalityArgumentCaptor.capture());
        Mortality mortalityUpdated = mortalityArgumentCaptor.getValue();

        assertEquals(mortality, mortalityUpdated);
    }

    @Test
    @DisplayName("When update an unknown record then throws not found exception")
    void updateNotFound() {
        when(mortalityRepository.findById(any(UUID.class))).thenThrow(new NotFoundException("Record not found"));
        assertThrows(NotFoundException.class, () -> service.update(UUID.randomUUID(), MortalityTableApiHelper.createMortality()));
    }

    @Test
    @DisplayName("When batch processing for CSV file")
    void saveRecords() {
        MortalityDto mortalityDto = new MortalityDto(
                null,
                "PT",
                2010,
                BigDecimal.valueOf(201.1),
                BigDecimal.valueOf(10.1)
        );

        try (MockedStatic<CsvHelper> csvHelperMockedStatic = Mockito.mockStatic(CsvHelper.class)) {
            csvHelperMockedStatic.when(() -> CsvHelper.csvToMortalityDtoList(any())).thenReturn(Arrays.asList(mortalityDto));
            service.saveRecords(file);

            verify(mortalityRepository, atLeastOnce()).deleteByCountryAndYear(any(String.class), anyInt());
            verify(mortalityRepository, atLeastOnce()).save(any(Mortality.class));
        }
    }

    @Test
    @DisplayName("when there is an exception during csv parse")
    void saveRecordsError() {
        assertThrows(RuntimeException.class, () -> service.saveRecords(file));
    }
}