package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn4Entity;
import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn8Entity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingProcessValidationOn4Repository extends
    JpaRepository<AccountingProcessValidationOn4Entity, String> {

  List<AccountingProcessValidationOn4Entity> findAccountingProcessValidationOn4EntitiesByIdProcess(String idProcess);

}
