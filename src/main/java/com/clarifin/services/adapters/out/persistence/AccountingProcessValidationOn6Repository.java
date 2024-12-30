package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn6Entity;
import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn8Entity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingProcessValidationOn6Repository extends
    JpaRepository<AccountingProcessValidationOn6Entity, String> {

  List<AccountingProcessValidationOn6Entity> findAccountingProcessValidationOn6EntitiesByIdProcess(String idProcess);

}
