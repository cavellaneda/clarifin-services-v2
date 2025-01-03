package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.ClientEntity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableDimensionsEntity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableEntity;
import com.clarifin.services.port.out.ClientPort;
import com.clarifin.services.port.out.PucPort;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PucAdapter implements PucPort {

  @Autowired
  private CuentaContableRepository cuentaContableRepository;

  @Autowired
  private CuentaContableDimensionsRepository cuentaContableDimensionsRepository;


  @Override
  public List<CuentaContableEntity> saveCuentasContables(List<CuentaContableEntity> cuentasContables) {
    return cuentaContableRepository.saveAll(cuentasContables);
  }

  @Override
  @Transactional
  public void deleteCuentasContables(String uuid) {
    cuentaContableRepository.deleteCuentaContableEntitiesByIdProcess(uuid);
  }

  @Override
  public List<CuentaContableDimensionsEntity> getCuentaContableDimensions(Long idClient,
      String idCompany, Date startDate, Date endDate) {
    return cuentaContableDimensionsRepository.findCuentaContableDimensionsEntitiesByIdClientAndIdCompanyAndDateProcessIsBetween(idClient, idCompany, startDate, endDate);
  }

  @Override
  public List<CuentaContableDimensionsEntity> getCuentaContableDimensions(Long idClient,
      String idCompany, String idProcess) {
    return cuentaContableDimensionsRepository.findCuentaContableDimensionsEntitiesByIdClientAndIdCompanyAndIdProcess(idClient, idCompany, idProcess);
  }


}
