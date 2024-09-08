package pt.bmo.mortalitytable_api.externalservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import pt.bmo.mortalitytable_api.externalservice.model.PopulationStatisticDto;
import pt.bmo.mortalitytable_api.externalservice.model.PopulationStatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface PopulationStatisticsMapper {

    PopulationStatisticsMapper INSTANCE = Mappers.getMapper(PopulationStatisticsMapper.class);

    default PopulationStatisticDto toDto(String countryCode, int year, List<PopulationStatistics> populationStatisticsList) {
        Map<String, Long> result = new HashMap<>();
        result.put("male", 0l);
        result.put("female", 0l);

        populationStatisticsList.stream()
                .forEach(populationStatistics -> {
                    Long currentFemaleCount = result.get("female");
                    currentFemaleCount += populationStatistics.females();
                    result.replace("female", currentFemaleCount);

                    Long currentMaleCount = result.get("male");
                    currentMaleCount += populationStatistics.males();
                    result.replace("male", currentMaleCount);
                });

        return new PopulationStatisticDto(countryCode, year, result.get("female"), result.get("male"));
    }


}
