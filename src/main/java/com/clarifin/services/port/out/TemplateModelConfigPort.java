package com.clarifin.services.port.out;

import com.clarifin.services.adapters.out.persistence.entities.TemplateModelConfigViewEntity;
import java.util.List;

public interface TemplateModelConfigPort {

  List<TemplateModelConfigViewEntity> findTemplateModelConfig(String idTemplateModel);
}
