package com.clarifin.services.domain.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.clarifin.services.adapters.out.persistence.entities.FormatFileEntity;
import com.clarifin.services.domain.Format;
import com.clarifin.services.domain.FormatFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;

@Validated
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileFormatMapper {

  FileFormatMapper INSTANCE = Mappers.getMapper(FileFormatMapper.class);

  @Mapping(source = "format", target = "format", qualifiedByName = "stringToMap")
  FormatFile entityToDomain(@NotNull FormatFileEntity formatFileEntity);

  @Mapping(source = "format", target = "format", qualifiedByName = "mapToString")
  FormatFileEntity domainToEntity(@NotNull FormatFile formatFile);

  @Named("stringToMap")
  static HashMap<String, Format> stringToMap(@NotNull String source) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(source, new TypeReference<HashMap<String, Format>>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Named("mapToString")
  static String mapToString(@NotNull HashMap<String, Format> source) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(source);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
