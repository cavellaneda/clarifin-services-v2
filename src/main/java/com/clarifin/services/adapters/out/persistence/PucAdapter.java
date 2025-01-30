package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.CuentaContableDimensionsEntity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableEntity;
import com.clarifin.services.port.out.PucPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
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

  @PersistenceContext
  private EntityManager entityManager;

  private static final int BATCH_SIZE = 50; // Tamaño del batch

  @Transactional
  public void batchInsert(List<CuentaContableEntity> entities) {
    for (int i = 0; i < entities.size(); i++) {
      entityManager.persist(entities.get(i));
      if (i > 0 && i % BATCH_SIZE == 0) {
        entityManager.flush();
        entityManager.clear();
      }
    }
    entityManager.flush();
    entityManager.clear();
  }


  @Override
  public List<CuentaContableEntity> saveCuentasContables(List<CuentaContableEntity> cuentasContables) {
    return cuentaContableRepository.saveAll(cuentasContables);
  }

  @Override
  @Transactional
  public void deleteCuentasContables(String uuid) {
    cuentaContableRepository.deleteOldRecords(uuid);
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
