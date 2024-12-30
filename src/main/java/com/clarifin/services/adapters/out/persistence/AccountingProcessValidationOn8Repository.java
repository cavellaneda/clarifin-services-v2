package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn6Entity;
import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn8Entity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingProcessValidationOn8Repository extends
    JpaRepository<AccountingProcessValidationOn8Entity, String> {

  List<AccountingProcessValidationOn8Entity> findAccountingProcessValidationOn8EntitiesByIdProcess(String idProcess);

}
