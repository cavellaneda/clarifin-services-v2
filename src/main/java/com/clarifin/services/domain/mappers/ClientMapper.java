package com.clarifin.services.domain.mappers;

import com.clarifin.services.adapters.out.persistence.entities.ClientEntity;
import com.clarifin.services.domain.Client;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;

@Validated
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

  ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

  Client entityToDomain(@NotNull ClientEntity user);

  ClientEntity domainToEntity(@NotNull Client userDTO);
}
