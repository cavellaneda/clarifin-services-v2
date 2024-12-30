package com.clarifin.services.port.in;

import com.clarifin.services.domain.ResultUploadProcess;
import com.clarifin.services.domain.UploadProperties;
import org.springframework.web.multipart.MultipartFile;

public interface PucUseCase {
  ResultUploadProcess uploadFile(MultipartFile file, UploadProperties uploadProperties);
}
