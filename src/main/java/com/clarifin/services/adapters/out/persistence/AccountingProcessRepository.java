package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessEntity;
import com.clarifin.services.adapters.out.persistence.entities.ClientEntity;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountingProcessRepository extends JpaRepository<AccountingProcessEntity, String> {

  Optional<AccountingProcessEntity> findAccountingProcessEntityByIdClientAndDateProcessAndStatusAndIdBusiness(Long idClient, Date dateProcess, String status, String idBusiness);

  List<AccountingProcessEntity> findAccountingProcessEntitiesByIdClientAndIdBusinessAndDateProcessIsBetweenAndStatusOrderByDateProcessAsc(Long idClient, String idBusiness, Date startDate, Date endDate, String status);
}
