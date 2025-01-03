package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.BusinessUnitEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessUnitRepository extends JpaRepository<BusinessUnitEntity, String> {

  List<BusinessUnitEntity> findBusinessUnitEntitiesByIdCompany(String id);

  void deleteAllByIdCompany(String id);
}
