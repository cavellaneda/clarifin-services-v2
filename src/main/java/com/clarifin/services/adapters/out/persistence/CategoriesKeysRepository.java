package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.CategoriesKeysEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesKeysRepository extends JpaRepository<CategoriesKeysEntity, String> {

  Optional<CategoriesKeysEntity> findCategoriesEntityByIdKey(String idKey);

  List<CategoriesKeysEntity> findCategoriesEntityByIdTemplateMasterCategories(String idTemplateMasterCategories);

}
