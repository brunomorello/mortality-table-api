package pt.bmo.mortalitytable_api.services.external;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.bmo.mortalitytable_api.externalservice.PopulationStatisticServiceImpl;
import pt.bmo.mortalitytable_api.externalservice.exception.ExternalSystemException;
import pt.bmo.mortalitytable_api.externalservice.model.PopulationStatisticDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PopulationStatisticServiceImplTest {

    @Autowired
    private PopulationStatisticServiceImpl service;

    @Test
    void findByCountryAndYear() {
        PopulationStatisticDto result = service.findByCountryAndYear("BR", 2018);
        assertTrue(result.malePopulation() > 0);
        assertNotNull(result);
    }

    @Test
    void findByCountrAndYearFail() {
        assertThrows(ExternalSystemException.class, () -> service.findByCountryAndYear("test", 2018));
    }
}