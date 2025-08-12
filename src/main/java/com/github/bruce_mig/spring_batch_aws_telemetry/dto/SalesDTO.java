package com.github.bruce_mig.spring_batch_aws_telemetry.dto;

public record SalesDTO(
        Long saleId,
        Long productId,
        Long customerId,
        String saleDate,
        Double saleAmount,
        String location,
        String country
) {}
