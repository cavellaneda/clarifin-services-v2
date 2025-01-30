package com.clarifin.services.port.in;

import com.clarifin.services.domain.DeleteCommand;
import com.clarifin.services.domain.ResultUploadProcess;
import com.clarifin.services.domain.UploadProperties;
import java.util.concurrent.CompletableFuture;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

public interface PucUseCase {
  CompletableFuture<ResultUploadProcess> uploadFile(MultipartFile file, UploadProperties uploadProperties, String processId);

  boolean deleteProcess(DeleteCommand deleteCommand);

  CompletableFuture<ResultUploadProcess> getUploadResult(String taskId);
}

