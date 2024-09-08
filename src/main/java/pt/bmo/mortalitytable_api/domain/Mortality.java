package pt.bmo.mortalitytable_api.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table
@Entity
public class Mortality {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, columnDefinition = "char(36)", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "country", length = 2, nullable = false)
    private String country;

    @NotNull
    @Positive
    @Column(name = "\"year\"", nullable = false)
    private int year;

    @NotNull
    @Min(value = 0, message = "Value cannot be lower than zero")
    @Max(value = 1000, message = "Max allowed value is 1000")
    @Column(name = "femaleTx", scale = 2, nullable = false)
    private BigDecimal femaleTx;

    @NotNull
    @Min(value = 0, message = "Value cannot be lower than zero")
    @Max(value = 1000, message = "Max allowed value is 1000")
    @Column(name = "maleTx", scale = 2, nullable = false)
    private BigDecimal maleTx;

    @Column(name = "femalePopulation", nullable = false)
    private long femalePopulation;

    @Column(name = "malePopulation", nullable = false)
    private long malePopulation;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
