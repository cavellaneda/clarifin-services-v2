package com.clarifin.services.port.in;

import com.clarifin.services.domain.FormatFile;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Validated
public interface FormatFileUseCase {

  List<FormatFile> findAllFormats();

  FormatFile findFormat(@NotNull Long id);

  List<FormatFile> findFormatByClient(@NotNull Long idClient);

  void createFormatFile(@NotNull FormatFile formatFile);

  void createFormatFileByClient(@NotNull Long idClient, @NotNull FormatFile formatFile);

  void deleteFormat(@NotNull Long id);
}
