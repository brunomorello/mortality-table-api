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

        Mortality mortality2 = Mortality.builder()
                .year(2017)
                .femalePopulation(1815331)
                .malePopulation(1874736)
                .country("BR")
                .femaleTx(BigDecimal.valueOf(7.29))
                .maleTx(BigDecimal.valueOf(10.21))
                .build();

        mortalityRepository.save(mortality);
        mortalityRepository.save(mortality2);
    }
}
