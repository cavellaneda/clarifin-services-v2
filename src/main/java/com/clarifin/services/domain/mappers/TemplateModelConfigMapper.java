package com.clarifin.services.domain.mappers;

import com.clarifin.services.adapters.out.persistence.entities.KeyEntity;
import com.clarifin.services.adapters.out.persistence.entities.TemplateModelConfigViewEntity;
import com.clarifin.services.domain.TemplateModelConfig;
import com.clarifin.services.domain.TemplateModelConfigByClient;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;

@Validated
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TemplateModelConfigMapper {

  TemplateModelConfigMapper INSTANCE = Mappers.getMapper(TemplateModelConfigMapper.class);

  @Mapping(source = "orderTemplate", target = "order")
  @Mapping(source = "ruleLevel", target = "ruleLevel", qualifiedByName = "stringToListEntity")
  TemplateModelConfig entityToDomain(@NotNull TemplateModelConfigViewEntity entity, @NotNull @Context Map<String, KeyEntity> levels);

  @Mapping(source = "orderTemplate", target = "order")
  @Mapping(source = "ruleLevel", target = "ruleLevel", qualifiedByName = "stringToListEntity")
  TemplateModelConfigByClient entityToDomainByClient(@NotNull TemplateModelConfigViewEntity entity, @NotNull @Context Map<String, KeyEntity> levels);


  @Named("stringToListEntity")
  default List<KeyEntity> stringToListEntity(final String ruleLevel, @Context final Map<String, KeyEntity> levels) {
    if (ruleLevel == null) {
      return List.of();
    }

    return Arrays.stream(ruleLevel.split(","))
        .map(levels::get) // Obtiene cada entidad del mapa
        .filter(Objects::nonNull) // Filtra nulos por si no existe alguna clave
        .toList();  // Convierte a lista
  }

}
