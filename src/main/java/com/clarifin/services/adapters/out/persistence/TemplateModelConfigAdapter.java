package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.TemplateModelConfigViewEntity;
import com.clarifin.services.adapters.out.persistence.entities.UserEntity;
import com.clarifin.services.port.out.TemplateModelConfigPort;
import com.clarifin.services.port.out.UserPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemplateModelConfigAdapter implements TemplateModelConfigPort {

  @Autowired
  private TemplateModelConfigViewRepository templateModelConfigViewRepository;


  @Override
  public List<TemplateModelConfigViewEntity> findTemplateModelConfig(String idTemplateModel) {
    return templateModelConfigViewRepository.findTemplateModelConfigViewEntitiesByIdTemplateModel(idTemplateModel);
  }
}
