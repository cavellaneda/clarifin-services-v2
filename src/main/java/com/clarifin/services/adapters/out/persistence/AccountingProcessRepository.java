package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessEntity;
import com.clarifin.services.adapters.out.persistence.entities.ClientEntity;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AccountingProcessRepository extends JpaRepository<AccountingProcessEntity, String> {

  Optional<AccountingProcessEntity> findAccountingProcessEntityByIdClientAndDateProcessAndStatusAndIdCompany(Long idClient, Date dateProcess, String status, String idBusiness);

  List<AccountingProcessEntity> findAccountingProcessEntitiesByIdClientAndIdCompanyAndDateProcessIsBetweenAndStatusOrderByDateProcessAsc(Long idClient, String idBusiness, Date startDate, Date endDate, String status);

  List<AccountingProcessEntity> findAccountingProcessEntitiesByIdClientAndIdCompanyOrderByDateProcessAsc(Long idClient, String idBusiness);

  List<AccountingProcessEntity> findAccountingProcessEntitiesByIdClientOrderByDateProcessAsc(Long idClient);


  void deleteAllByIdAndIdClientAndIdCompany(String id, Long idClient, String idBusiness);
}
