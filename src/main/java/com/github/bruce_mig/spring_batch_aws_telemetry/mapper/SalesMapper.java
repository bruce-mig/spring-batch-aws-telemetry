package com.github.bruce_mig.spring_batch_aws_telemetry.mapper;

import com.github.bruce_mig.spring_batch_aws_telemetry.domain.SalesInfo;
import com.github.bruce_mig.spring_batch_aws_telemetry.dto.SalesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface SalesMapper {

    SalesInfo mapToEntity(SalesDTO salesDTO);
}
