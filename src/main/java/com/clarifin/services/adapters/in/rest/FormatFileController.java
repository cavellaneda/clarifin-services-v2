package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.Client;
import com.clarifin.services.domain.FormatFile;
import com.clarifin.services.port.in.ClientUseCase;
import com.clarifin.services.port.in.FormatFileUseCase;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/entity/format-file")
public class FormatFileController {

  @Autowired
  private FormatFileUseCase formatFileUseCase;

  @GetMapping
  public List<FormatFile> getAllFormats() {
    return formatFileUseCase.findAllFormats();
  }

  @GetMapping("/{id}")
  public FormatFile getFormat(@PathVariable Long id) {
    return formatFileUseCase.findFormat(id);
  }

  @GetMapping("/client/{id}")
  public List<FormatFile> getFormatByClient(@PathVariable Long id) {
    return formatFileUseCase.findFormatByClient(id);
  }

  @PostMapping
  public void createFormat(@RequestBody FormatFile formatFile) {
    formatFileUseCase.createFormatFile(formatFile);
  }

  @PostMapping("/client/{id}")
  public void createFormatByClient(@PathVariable Long id, @RequestBody FormatFile formatFile) {
    formatFileUseCase.createFormatFileByClient(id, formatFile);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFormat(@PathVariable Long id) {
    formatFileUseCase.deleteFormat(id);
    return ResponseEntity.noContent().build();
  }
}
