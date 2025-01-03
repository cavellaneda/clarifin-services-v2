package com.clarifin.services.port.out;

import com.clarifin.services.adapters.out.persistence.entities.TemplateModelEntity;
import com.clarifin.services.domain.TemplateModel;
import java.util.Collection;
import java.util.List;

public interface TemplateModelPort {

  List<TemplateModelEntity> findAllTemplateModelBase();

  TemplateModelEntity findByTemplateModelId(String idTemplateModel);
}
