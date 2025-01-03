package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.TransactionalConfirmationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionalConfirmationRepository extends
    JpaRepository<TransactionalConfirmationEntity, String> {

  List<TransactionalConfirmationEntity> findTransactionalConfirmationEntitiesByIdProcessAndIdBusinessUnit(
      String idProcess, String idBusinessUnit);

}
