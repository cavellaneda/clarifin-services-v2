package com.clarifin.services.domain.mappers;

import com.clarifin.services.adapters.out.persistence.entities.BusinessUnitEntity;
import com.clarifin.services.adapters.out.persistence.entities.CompanyEntity;
import com.clarifin.services.domain.BusinessUnit;
import com.clarifin.services.domain.Company;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;

@Validated
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyMapper {

  CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

  Company entityToDomain(@NotNull CompanyEntity entity);

  CompanyEntity domainToEntity(@NotNull Company domain);

  List<BusinessUnit> businessUnitEntityToDomain(List<BusinessUnitEntity> allBusinessUnitByCompanyId);
}
