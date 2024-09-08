package pt.bmo.mortalitytable_api.externalservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PopulationStatistics(@JsonProperty("country") String country,
                                   @JsonProperty("males") long males,
                                   @JsonProperty("females") long females,
                                   @JsonProperty("age") int age,
                                   @JsonProperty("year") int year,
                                   @JsonProperty("total") long total) {
}
