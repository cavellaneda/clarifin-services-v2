package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.CategoriesKeyEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesKeyRepository extends
    JpaRepository<CategoriesKeyEntity, String> {

  List<CategoriesKeyEntity> findCategoriesKeyEntitiesByIdTemplateMasterCategoriesIn(
      List<String> templateMasterCategoriesIds);


}
