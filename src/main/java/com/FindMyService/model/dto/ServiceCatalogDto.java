package com.FindMyService.model.dto;

import com.FindMyService.model.enums.Availability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCatalogDto {
    private Long serviceId;
    private Long providerId;
    private String providerName;
    private String serviceName;
    private String description;
    private BigDecimal cost;
    private String location;
    private Availability availability;
    private Integer warrantyPeriodMonths;
    private String imageUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean active;
    private BigDecimal avgRating;
    private Integer totalRatings;
}
