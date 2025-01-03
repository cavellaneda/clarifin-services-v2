package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessEntity;
import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn2Entity;
import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn4Entity;
import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn6Entity;
import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessValidationOn8Entity;
import com.clarifin.services.adapters.out.persistence.entities.BalanceComparisonValidationEntity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableCategoriesEntity;
import com.clarifin.services.adapters.out.persistence.entities.TransactionalConfirmationEntity;
import com.clarifin.services.domain.UploadProperties;
import com.clarifin.services.port.out.AccountingProcessPort;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccoutingProcessAdapter implements AccountingProcessPort {

  @Autowired
  private AccountingProcessRepository accountingProcessRepository;

  @Autowired
  private TransactionalConfirmationRepository transactionalConfirmationRepository;

  @Autowired
  private BalanceComparisonValidationRepository balanceComparisonValidationRepository;

  @Autowired
  private CuentaContableCategoriesRepository cuentaContableCategoriesRepository;

  @Autowired
  private CuentaContableRepository cuentaContableRepository;

  @Autowired
  private AccountingProcessValidationOn2Repository accountingProcessValidationOn2Repository;

  @Autowired
  private AccountingProcessValidationOn4Repository accountingProcessValidationOn4Repository;

  @Autowired
  private AccountingProcessValidationOn6Repository accountingProcessValidationOn6Repository;

  @Autowired
  private AccountingProcessValidationOn8Repository accountingProcessValidationOn8Repository;

  @Override
  public void saveProcess(String uuid, UploadProperties uploadProperties, List<String> idBusinessUnits, String status) {
    accountingProcessRepository.save(
        AccountingProcessEntity.builder()
            .id(uuid)
            .idClient(uploadProperties.getIdClient())
            .idFormat(uploadProperties.getIdFormat())
            .dateProcess(uploadProperties.getDateImport())
            .status(status)
            .idCompany(uploadProperties.getIdCompany())
            .idBusinessUnit(new Gson().toJson(idBusinessUnits))
            .build()
    );
  }

  @Override
  public List<String> validateProcess(String uuid) {

    final List<String> errors = new ArrayList<>();

    final String msgError = "Error en la validación de la etapa: %s, en el codigo PUC: %s, valores erroneos: valor esperado %s, valor obtenido %s";

    List<AccountingProcessValidationOn8Entity> resultsOn8 = accountingProcessValidationOn8Repository.findAccountingProcessValidationOn8EntitiesByIdProcess(uuid);

    if (!resultsOn8.isEmpty()) {
      resultsOn8.stream().forEach(resultOn8 -> {
        if (resultOn8.getTotalValue6() != null) {
          if (!"MATCH".equals(resultOn8.getValidationResult())) {
            errors.add(
                String.format(msgError, "8", resultOn8.getLevelCode(), resultOn8.getTotalValue8(),
                    resultOn8.getTotalValue6()));
          }
        }
      });
      if (!errors.isEmpty()) {
        return errors;
      } else {
        List<AccountingProcessValidationOn6Entity> resultsOn6 = accountingProcessValidationOn6Repository.findAccountingProcessValidationOn6EntitiesByIdProcess(
            uuid);
        if (!resultsOn6.isEmpty()) {
          resultsOn6.stream().forEach(resultOn6 -> {
            if (resultOn6.getTotalValue4() != null) {
              if (!"MATCH".equals(resultOn6.getValidationResult())) {
                errors.add(
                    String.format(msgError, "6", resultOn6.getLevelCode(),
                        resultOn6.getTotalValue6(),
                        resultOn6.getTotalValue4()));
              }
            }
          });
          if (!errors.isEmpty()) {
            return errors;
          } else {
            List<AccountingProcessValidationOn4Entity> resultsOn4 = accountingProcessValidationOn4Repository.findAccountingProcessValidationOn4EntitiesByIdProcess(
                uuid);
            if (!resultsOn4.isEmpty()) {
              resultsOn4.stream().forEach(resultOn4 -> {
                if (resultOn4.getTotalValue2() != null) {
                  if (!"MATCH".equals(resultOn4.getValidationResult())) {
                    errors.add(
                        String.format(msgError, "4", resultOn4.getLevelCode(),
                            resultOn4.getTotalValue4(),
                            resultOn4.getTotalValue2()));
                  }
                }
              });
              if (!errors.isEmpty()) {
                return errors;
              } else {
                List<AccountingProcessValidationOn2Entity> resultsOn2 = accountingProcessValidationOn2Repository.findAccountingProcessValidationOn2EntitiesByIdProcess(
                    uuid);
                if (!resultsOn2.isEmpty()) {
                  resultsOn2.stream().forEach(resultOn2 -> {
                    if (resultOn2.getTotalValue1() != null) {
                      if (!"MATCH".equals(resultOn2.getValidationResult())) {
                        errors.add(
                            String.format(msgError, "2", resultOn2.getLevelCode(),
                                resultOn2.getTotalValue2(),
                                resultOn2.getTotalValue1()));
                      }
                    }
                  });
                }
                else{
                  return errors;
                }
              }

            }
          }
        }
      }
    }

    return errors;
  }

  @Override
  public void updateToError(String uuid, List<String> error) {
    final AccountingProcessEntity process = accountingProcessRepository.getById(uuid);
    process.setStatus("ERROR");
    process.setErrorDescription(error.toString());
    accountingProcessRepository.save(process);
  }

  @Override
  public void updateToSuccess(String uuid) {
    final AccountingProcessEntity process = accountingProcessRepository.getById(uuid);
    process.setStatus("SUCCESS");
    accountingProcessRepository.save(process);
  }

  @Override
  public Optional<AccountingProcessEntity> getProcessByIdClientAndDateProcessAndStateAndBusiness(Long idClient, Date dateImport,
      String status, String idBusiness) {
    return accountingProcessRepository.findAccountingProcessEntityByIdClientAndDateProcessAndStatusAndIdCompany(idClient, dateImport, status, idBusiness);
  }

  @Override
  public List<TransactionalConfirmationEntity> getTransactionalConfirmationByIdProcessAndIdBusinessUnit(String idProcess, String idBusinessUnit) {
    return transactionalConfirmationRepository.findTransactionalConfirmationEntitiesByIdProcessAndIdBusinessUnit(idProcess, idBusinessUnit);
  }

  @Override
  public List<AccountingProcessEntity> getProcess(Long idClient, String idBusiness, Date startDate,
      Date endDate) {
    return accountingProcessRepository.findAccountingProcessEntitiesByIdClientAndIdCompanyAndDateProcessIsBetweenAndStatusOrderByDateProcessAsc(idClient, idBusiness, startDate, endDate, "SUCCESS");
  }

  @Override
  public List<AccountingProcessEntity> getProcess(Long idClient, String idCompany) {
    return accountingProcessRepository.findAccountingProcessEntitiesByIdClientAndIdCompanyOrderByDateProcessAsc(idClient, idCompany);
  }

  @Override
  public List<AccountingProcessEntity> getProcess(Long idClient) {
    return accountingProcessRepository.findAccountingProcessEntitiesByIdClientOrderByDateProcessAsc(idClient);
  }


  @Override
  public List<String> getBalanceComparison(String idProcessPrevious, String idProcessCurrent) {
    final List<String> errors = new ArrayList<>();

    final String msgError = "Error en la validación de saldos: en el codigo PUC: %s, valores erroneos: valor Saldo final mes anterior %s, valor saldo inicial mes cargado %s";
    List<BalanceComparisonValidationEntity> result = balanceComparisonValidationRepository.findBalanceComparison(idProcessPrevious, idProcessCurrent);

    result.forEach(balanceComparisonValidationEntity -> {
      if (!"Match".equals(balanceComparisonValidationEntity.getValidation())) {
        if(balanceComparisonValidationEntity.getFinalBalanceAnterior() != null){
          errors.add(
              String.format(msgError, balanceComparisonValidationEntity.getCode(),
                  balanceComparisonValidationEntity.getFinalBalanceAnterior(),
                  balanceComparisonValidationEntity.getInitialBalanceActual()));
        }
      }
    });

    return errors;
  }

  @Override
  public void deleteProcess(String idProcess, Long idClient, String idBusiness) {
      System.out.println(LocalDateTime.now() + " - Deleting process: " + idProcess);

      deleteCategoriasContables(idProcess);

      System.out.println(LocalDateTime.now() + " - Deleting process: " + idProcess);


      deleteProcessBd(idProcess, idClient, idBusiness);

      System.out.println(LocalDateTime.now() + " - Deleting process: " + idProcess);
  }

  @Transactional
  protected void deleteProcessBd(String idProcess, Long idClient, String idBusiness) {
    updateToError(idProcess, List.of("Process deleted"));
  }

  @Transactional
  protected void deleteCategoriasContables(String idProcess) {
    cuentaContableRepository.deleteOldRecords(idProcess);
  }

  @Override
  public List<CuentaContableCategoriesEntity> getCuentaContableCategories(String idCategoryTemplate, String idProcess) {
    return null;//cuentaContableCategoriesRepository.findByCategoryTemplatePrincipalQuery(idProcess, idCategoryTemplate);
  }
}
