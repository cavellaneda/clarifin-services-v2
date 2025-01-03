package com.clarifin.services.port.out;

import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessEntity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableCategoriesEntity;
import com.clarifin.services.adapters.out.persistence.entities.TransactionalConfirmationEntity;
import com.clarifin.services.domain.UploadProperties;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface AccountingProcessPort {

  void saveProcess(String uuid, UploadProperties uploadProperties, List<String> idBusinessUnits, String status);

  List<String> validateProcess(String uuid);

  void updateToError(String uuid, List<String> error);

  void updateToSuccess(String uuid);

  Optional<AccountingProcessEntity> getProcessByIdClientAndDateProcessAndStateAndBusiness(Long idClient, Date dateImport, String status, String idBusiness);


  List<TransactionalConfirmationEntity> getTransactionalConfirmationByIdProcessAndIdBusinessUnit(String idProcess, String idBusinessUnit);

  List<AccountingProcessEntity> getProcess(Long idClient, String idCompany, Date startDate, Date endDate);

  List<AccountingProcessEntity> getProcess(Long idClient, String idCompany);

  List<AccountingProcessEntity> getProcess(Long idClient);

  List<String> getBalanceComparison(String idProcessPrevious, String idProcessCurrent);

  void deleteProcess(String idProcess, Long idClient, String idBusiness);


  List<CuentaContableCategoriesEntity> getCuentaContableCategories(String idCategoryTemplate,
      String idProcess);
}
