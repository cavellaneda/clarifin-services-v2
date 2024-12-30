package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.FormatFileEntity;
import com.clarifin.services.port.out.FormatFilePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormatFileAdapter implements FormatFilePort {

  @Autowired
  private final FormatFileRepository formatFileRepository;

  @Override
  public List<FormatFileEntity> findAllFormats() {
    return formatFileRepository.findAll();
  }

  @Override
  public FormatFileEntity findFormat(Long id) {
    return formatFileRepository.findById(id).orElse(null);
  }

  @Override
  public List<FormatFileEntity> findAllFormatsByClient(Long idClient) {
    return formatFileRepository.findByIdClient(idClient);
  }

  @Override
  public void createFormatFile(FormatFileEntity formatFileEntity) {
    formatFileRepository.save(formatFileEntity);
  }

  @Override
  public void createFormatFileToClient(Long idClient, FormatFileEntity formatFileEntity) {
    formatFileEntity.setIdClient(idClient);
    formatFileRepository.save(formatFileEntity);
  }

  @Override
  public void deleteFormat(Long id) {
    formatFileRepository.deleteById(id);
  }
}
