package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.TemplateModelConfigViewEntity;
import com.clarifin.services.adapters.out.persistence.entities.TransactionalConfirmationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateModelConfigViewRepository extends
    JpaRepository<TemplateModelConfigViewEntity, String> {

  List<TemplateModelConfigViewEntity> findTemplateModelConfigViewEntitiesByIdTemplateModel(
      String idTemplateModel);

}
