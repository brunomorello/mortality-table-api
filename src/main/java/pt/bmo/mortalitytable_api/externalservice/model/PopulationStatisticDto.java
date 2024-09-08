package pt.bmo.mortalitytable_api.externalservice.model;

public record PopulationStatisticDto(String country,
                                     int year,
                                     long femalePopulation,
                                     long malePopulation) { }
