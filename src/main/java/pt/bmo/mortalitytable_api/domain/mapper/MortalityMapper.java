package pt.bmo.mortalitytable_api.domain.mapper;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pt.bmo.mortalitytable_api.controllers.dto.MortalityDto;
import pt.bmo.mortalitytable_api.domain.Mortality;

import java.math.BigDecimal;

@Mapper
public interface MortalityMapper {

    MortalityMapper INSTANCE = Mappers.getMapper(MortalityMapper.class);

    @Mapping(source = "country", target = "country")
    @Mapping(source = "year", target = "year")
    @Mapping(source = "femaleTx", target = "femaleTx")
    @Mapping(source = "maleTx", target = "maleTx")
    MortalityDto toDto(Mortality mortality);

    @Mapping(source = "country", target = "country")
    @Mapping(source = "year", target = "year")
    @Mapping(source = "femaleTx", target = "femaleTx")
    @Mapping(source = "maleTx", target = "maleTx")
    @Mapping(target = "femalePopulation", ignore = true)
    @Mapping(target = "malePopulation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Mortality toModel(MortalityDto mortalityDto);
}
