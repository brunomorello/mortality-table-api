package pt.bmo.mortalitytable_api.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pt.bmo.mortalitytable_api.domain.Mortality;
import pt.bmo.mortalitytable_api.repositories.MortalityRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final MortalityRepository mortalityRepository;

    @Override
    public void run(String... args) throws Exception {

        Mortality mortality = Mortality.builder()
                .year(2016)
                .femalePopulation(5427117)
                .malePopulation(4882456)
                .country("PT")
                .femaleTx(BigDecimal.valueOf(3.21))
                .maleTx(BigDecimal.valueOf(4.5))
                .build();

        mortalityRepository.save(mortality);
    }
}
