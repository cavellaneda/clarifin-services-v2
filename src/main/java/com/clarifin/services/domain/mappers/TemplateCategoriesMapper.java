package com.clarifin.services.domain.mappers;

import com.clarifin.services.adapters.out.persistence.entities.TemplateMasterCategoriesKeysEntity;
import com.clarifin.services.domain.TemplateCategories;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;

@Validated
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TemplateCategoriesMapper {

  TemplateCategoriesMapper INSTANCE = Mappers.getMapper(TemplateCategoriesMapper.class);

  @Mapping(source = "typeIndustry", target = "industry")
  TemplateCategories entityToDomain(@NotNull TemplateMasterCategoriesKeysEntity entity);

  @Mapping(source = "industry", target = "typeIndustry")
  TemplateMasterCategoriesKeysEntity domainToEntity(@NotNull TemplateCategories domain);


}
