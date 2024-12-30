package com.clarifin.services.services;

import com.clarifin.services.domain.FormatFile;
import com.clarifin.services.domain.mappers.FileFormatMapper;
import com.clarifin.services.port.in.FormatFileUseCase;
import com.clarifin.services.port.out.FormatFilePort;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormatFileService implements FormatFileUseCase {

  @Autowired
  private FormatFilePort formatFilePort;

  @Override
  public List<FormatFile> findAllFormats() {
    return formatFilePort.findAllFormats().stream().map(
              FileFormatMapper.INSTANCE::entityToDomain
            ).collect(Collectors.toList());
  }

  @Override
  public FormatFile findFormat(Long id) {
    return FileFormatMapper.INSTANCE.entityToDomain(formatFilePort.findFormat(id));
  }

  @Override
  public List<FormatFile> findFormatByClient(Long idClient) {
    return formatFilePort.findAllFormatsByClient(idClient).stream().map(
        FileFormatMapper.INSTANCE::entityToDomain
    ).collect(Collectors.toList());
  }

  @Override
  public void createFormatFile(FormatFile formatFile) {
    formatFilePort.createFormatFile(FileFormatMapper.INSTANCE.domainToEntity(formatFile));

  }

  @Override
  public void createFormatFileByClient(Long idClient, FormatFile formatFile) {
    formatFilePort.createFormatFileToClient(idClient, FileFormatMapper.INSTANCE.domainToEntity(formatFile));

  }

  @Override
  public void deleteFormat(Long id) {
    formatFilePort.deleteFormat(id);
  }
}
