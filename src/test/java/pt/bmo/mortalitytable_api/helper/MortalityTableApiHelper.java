package pt.bmo.mortalitytable_api.helper;

import pt.bmo.mortalitytable_api.domain.Mortality;

import java.time.LocalDate;
import java.util.UUID;

public class MortalityTableApiHelper {

    private MortalityTableApiHelper() { }

    public static Mortality createMortality() {
        return Mortality.builder()
                .id(UUID.randomUUID())
                .year(LocalDate.now().getYear())
                .country("PT")
                .build();
    }
}
