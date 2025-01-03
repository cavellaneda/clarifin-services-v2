package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.CuentaContableDimensionsEntity;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaContableDimensionsRepository extends
    JpaRepository<CuentaContableDimensionsEntity, String> {

  List<CuentaContableDimensionsEntity> findCuentaContableDimensionsEntitiesByIdClientAndIdCompanyAndDateProcessIsBetween(
      Long idClient, String idCompany, Date startDate, Date endDate);

  List<CuentaContableDimensionsEntity> findCuentaContableDimensionsEntitiesByIdClientAndIdCompanyAndIdProcess(
      Long idClient, String idCompany, String idProcess);
}
