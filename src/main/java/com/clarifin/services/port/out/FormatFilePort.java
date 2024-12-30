package com.clarifin.services.port.out;

import com.clarifin.services.adapters.out.persistence.entities.FormatFileEntity;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Validated
public interface FormatFilePort {

  List<FormatFileEntity> findAllFormats();

  FormatFileEntity findFormat(@NotNull Long id);

  List<FormatFileEntity> findAllFormatsByClient(@NotNull Long idClient);

  void createFormatFile(@NotNull FormatFileEntity formatFileEntity);

  void createFormatFileToClient(@NotNull Long idClient, @NotNull FormatFileEntity formatFileEntity);

  void deleteFormat(@NotNull Long id);
}
