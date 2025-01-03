package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.TemplateModelConfigViewEntity;
import com.clarifin.services.adapters.out.persistence.entities.TemplateModelEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateModelRepository extends
    JpaRepository<TemplateModelEntity, String> {

  List<TemplateModelEntity> findTemplateModelEntitiesByBase(
      boolean isBase);

}
