package pt.bmo.mortalitytable_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pt.bmo.mortalitytable_api.controllers.dto.MortalityDto;
import pt.bmo.mortalitytable_api.domain.Mortality;
import pt.bmo.mortalitytable_api.repositories.MortalityRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class MortalityControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MortalityRepository mortalityRepository;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Mortality testMortality;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .build();
        testMortality = mortalityRepository.findAll().iterator().next();
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(get(MortalityController.BASE_URL)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @Test
    void getByYear() throws Exception {
        mockMvc.perform(get(MortalityController.BASE_URL + "/{year}", testMortality.getYear())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].year").value(testMortality.getYear()));
    }

    @Test
    void create() throws Exception {
        MortalityDto mortalityDto = new MortalityDto(null,
                "BR",
                2001,
                BigDecimal.valueOf(5.20),
                BigDecimal.valueOf(10.22));

        mockMvc.perform(post(MortalityController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mortalityDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createWithWrongValues() throws Exception {
        MortalityDto mortalityDto = new MortalityDto(null,
                null,
                2001,
                BigDecimal.valueOf(-5.20),
                BigDecimal.valueOf(1000000000));

        mockMvc.perform(post(MortalityController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(mortalityDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        final BigDecimal femaleTx = BigDecimal.valueOf(5.20);
        final BigDecimal maleTx = BigDecimal.valueOf(10.22);

        MortalityDto mortalityDto = new MortalityDto(testMortality.getId(),
                testMortality.getCountry(),
                testMortality.getYear(),
                femaleTx,
                maleTx);

        mockMvc.perform(put(MortalityController.BASE_URL + "/{id}", testMortality.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mortalityDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.femaleTx").value(femaleTx))
                .andExpect(jsonPath("$.maleTx").value(maleTx));
    }

    @Test
    void updateNotFound() throws Exception {
        MortalityDto mortalityDto = new MortalityDto(null,
                "BR",
                2001,
                BigDecimal.valueOf(5.20),
                BigDecimal.valueOf(10.22));

        mockMvc.perform(put(MortalityController.BASE_URL + "/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(mortalityDto)))
                .andExpect(status().isNotFound());
    }
}