package pt.bmo.mortalitytable_api.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record MortalityDto(UUID id,
                           @NotNull
                           String country,
                           @NotNull
                           @Positive
                           int year,
                           @NotNull
                           @Min(value = 0, message = "Value cannot be lower than zero")
                           @Max(value = 1000, message = "Max allowed value is 1000")
                           BigDecimal femaleTx,
                           @NotNull
                           @Min(value = 0, message = "Value cannot be lower than zero")
                           @Max(value = 1000, message = "Max allowed value is 1000")
                           BigDecimal maleTx) {
}
