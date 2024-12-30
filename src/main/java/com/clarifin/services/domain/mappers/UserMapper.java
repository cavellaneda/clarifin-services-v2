package com.clarifin.services.domain.mappers;

import com.clarifin.services.adapters.out.persistence.entities.UserEntity;
import com.clarifin.services.domain.User;
import com.clarifin.services.domain.UserComplete;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;

@Validated
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  User entityToDomain(@NotNull UserEntity entity);

  UserEntity domainToEntity(@NotNull User domain);

  UserEntity domainToEntity(@NotNull UserComplete domain);
}
