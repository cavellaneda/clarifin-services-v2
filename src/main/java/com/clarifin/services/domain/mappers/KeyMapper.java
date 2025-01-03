package com.clarifin.services.domain.mappers;

import com.clarifin.services.adapters.out.persistence.entities.KeyEntity;
import com.clarifin.services.domain.Key;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;

@Validated
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KeyMapper {

  KeyMapper INSTANCE = Mappers.getMapper(KeyMapper.class);

  Key entityToDomain(@NotNull KeyEntity entity);

  KeyEntity domainToEntity(@NotNull Key domain);


}
