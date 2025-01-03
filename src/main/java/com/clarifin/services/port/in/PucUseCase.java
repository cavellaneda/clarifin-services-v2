package com.clarifin.services.port.in;

import com.clarifin.services.domain.DeleteCommand;
import com.clarifin.services.domain.ResultUploadProcess;
import com.clarifin.services.domain.UploadProperties;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

public interface PucUseCase {
  ResultUploadProcess uploadFile(MultipartFile file, UploadProperties uploadProperties);

  boolean deleteProcess(DeleteCommand deleteCommand);
}

