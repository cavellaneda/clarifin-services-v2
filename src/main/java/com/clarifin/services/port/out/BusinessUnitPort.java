package com.clarifin.services.port.out;

import com.clarifin.services.adapters.out.persistence.entities.BusinessUnitEntity;
import com.clarifin.services.domain.BusinessUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BusinessUnitPort {

  Optional<BusinessUnitEntity> findByClientAndIdCompanyAndBusinessUnit(Long idClient, String idCompany, String idBusinessUnit);

  List<BusinessUnitEntity> findAllBusinessUnitByCompanyId(String id);

  void createBusinessUnitList(String id, List<BusinessUnit> businessUnits);

  void deleteBusinessUnitByCompanyId(String idCompany);
}
