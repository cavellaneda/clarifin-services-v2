package com.clarifin.services.domain.mappers;

import com.clarifin.services.adapters.out.persistence.entities.BusinessEntity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableDimensionsEntity;
import com.clarifin.services.domain.Business;
import com.clarifin.services.domain.CuentaContableDimensions;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;

@Validated
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CuentaContableDimensionsMapper {

  CuentaContableDimensionsMapper INSTANCE = Mappers.getMapper(CuentaContableDimensionsMapper.class);

  CuentaContableDimensions entityToDomain(@NotNull CuentaContableDimensionsEntity entity);

  CuentaContableDimensionsEntity domainToEntity(@NotNull CuentaContableDimensions domain);
}
