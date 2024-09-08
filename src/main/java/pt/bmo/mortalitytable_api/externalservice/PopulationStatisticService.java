package pt.bmo.mortalitytable_api.externalservice;

import pt.bmo.mortalitytable_api.externalservice.model.PopulationStatisticDto;

public interface PopulationStatisticService {
    PopulationStatisticDto findByCountryAndYear(String countryCode, int year);
}
