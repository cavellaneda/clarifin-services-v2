package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn2Entity;
import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn8Entity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingProcessValidationOn2Repository extends
    JpaRepository<AccountingProcessValidationOn2Entity, String> {

  List<AccountingProcessValidationOn2Entity> findAccountingProcessValidationOn2EntitiesByIdProcess(String idProcess);

}
