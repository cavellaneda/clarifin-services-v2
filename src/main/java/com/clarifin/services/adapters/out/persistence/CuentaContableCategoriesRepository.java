package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.CuentaContableCategoriesEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaContableCategoriesRepository extends
    JpaRepository<CuentaContableCategoriesEntity, String> {

  List<CuentaContableCategoriesEntity> findDistinctByIdProcessAndTransactionalAndCategoryTemplate(
      String idProcess, String transactional, String categoryTemplate);

  List<CuentaContableCategoriesEntity> findDistinctByIdProcessAndTransactionalAndCategoryTemplateIsNull(
      String idProcess, String transactional);

  List<CuentaContableCategoriesEntity> findCuentaContableCategoriesEntitiesByIdProcessAndTransactionalAndCategoryTemplateAndRegistroTipo(
      String idProcess, String transactional, String categoryTemplate, String registroTipo);

  void deleteAllByIdProcessIn(List<String> idProcess);

}
