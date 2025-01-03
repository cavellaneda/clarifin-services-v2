package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.TemplateMasterCategoriesKeysEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateMasterCategoriesRepository extends
    JpaRepository<TemplateMasterCategoriesKeysEntity, String> {

  List<TemplateMasterCategoriesKeysEntity> findTemplateMasterCategoriesEntitiesByIdCompany(String idBusiness);

  List<TemplateMasterCategoriesKeysEntity> findTemplateMasterCategoriesEntitiesByIdCompanyAndIdBusinessUnit(String idBusiness, String idBusinessUnit);

  List<TemplateMasterCategoriesKeysEntity> findTemplateMasterCategoriesEntitiesByTypeIndustryInAndIdCompanyIsNull(List<String> typeIndustry);

  List<TemplateMasterCategoriesKeysEntity> findTemplateMasterCategoriesEntitiesByIdCompanyIsNull();

  List<TemplateMasterCategoriesKeysEntity> findTemplateMasterCategoriesEntitiesByIdCompanyIn(List<String> idBusiness);

  Optional<TemplateMasterCategoriesKeysEntity> findTemplateMasterCategoriesEntityByIdAndIdCompany(String id, String idBusiness);
}
