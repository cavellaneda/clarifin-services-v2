package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.TemplateModelEntity;
import com.clarifin.services.port.out.TemplateModelConfigPort;
import com.clarifin.services.port.out.TemplateModelPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemplateModelAdapter implements TemplateModelPort {

  @Autowired
  private TemplateModelRepository templateModelRepository;


  @Override
  public List<TemplateModelEntity> findAllTemplateModelBase() {
    return templateModelRepository.findTemplateModelEntitiesByBase(true);
  }

  @Override
  public TemplateModelEntity findByTemplateModelId(String idTemplateModel) {
    return templateModelRepository.getById(idTemplateModel);
  }
}
