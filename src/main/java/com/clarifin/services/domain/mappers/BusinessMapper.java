package com.clarifin.services.domain.mappers;

import com.clarifin.services.adapters.out.persistence.entities.BusinessEntity;
import com.clarifin.services.domain.Business;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;

@Validated
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BusinessMapper {

  BusinessMapper INSTANCE = Mappers.getMapper(BusinessMapper.class);

  Business entityToDomain(@NotNull BusinessEntity entity);

  BusinessEntity domainToEntity(@NotNull Business domain);
}