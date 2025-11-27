package com.FindMyService.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Entity
public class LineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lineItemId;

    @NonNull
    private String serviceName;

    @NonNull
    private BigDecimal cost;

    private String imageUrl;

    @Min(1)
    private Integer quantityUnits;

    private LocalDate requestedDate;
    private LocalDate scheduledDate;
}
