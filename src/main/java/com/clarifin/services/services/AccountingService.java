package com.clarifin.services.services;

import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessEntity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableDimensionsEntity;
import com.clarifin.services.adapters.out.persistence.entities.TransactionalConfirmationEntity;
import com.clarifin.services.domain.Accounting;
import com.clarifin.services.domain.AccountingResponse;
import com.clarifin.services.domain.CuentaContableDimensions;
import com.clarifin.services.domain.ToWriteCsv;
import com.clarifin.services.domain.mappers.BusinessMapper;
import com.clarifin.services.domain.mappers.CuentaContableDimensionsMapper;
import com.clarifin.services.port.in.AccountingUseCase;
import com.clarifin.services.port.out.AccountingProcessPort;
import com.clarifin.services.port.out.PucPort;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountingService implements AccountingUseCase {

  final CuentaContableDimensionsMapper mapper = CuentaContableDimensionsMapper.INSTANCE;

  @Autowired
  private PucPort pucPort;

  @Autowired
  private AccountingProcessPort accountingProcessPort;

  @Override
  public AccountingResponse getAccountingByDates(Long idClient, String idBusiness, Date startDate,
      Date endDate) {
    List<CuentaContableDimensionsEntity> result = pucPort.getCuentaContableDimensions(idClient, idBusiness, startDate, endDate);

    List<AccountingProcessEntity> processList = accountingProcessPort.getProcess(idClient, idBusiness, startDate, endDate);

    List<Accounting> accountingList = new ArrayList<>();
    Map<String, Accounting> accountingMap = new HashMap<>();

    for (CuentaContableDimensionsEntity entity : result) {
      String code = String.valueOf(entity.getCode());
      Accounting accounting = accountingMap.getOrDefault(code, new Accounting());
      accounting.setCode(code);
      accounting.setDescription(entity.getDescription());
      accounting.setTransactional(entity.getTransactional());

      if (accounting.getBalances() == null) {
        accounting.setBalances(new HashMap<>());
      }

      String dateKey = new SimpleDateFormat("yyyy-MM-dd").format(entity.getDateProcess());
      accounting.getBalances().put(dateKey, entity.getFinalBalance());

      accountingMap.put(code, accounting);
    }

    accountingList.addAll(accountingMap.values());

    accountingList.sort(Comparator.comparing(Accounting::getCode));

    return AccountingResponse.builder()
        .accounting(accountingList)
        .columnsName(processList.stream().map(accountingProcessEntity -> {
          String dateKey = new SimpleDateFormat("yyyy-MM-dd").format(accountingProcessEntity.getDateProcess());
          return dateKey;
        }).collect(Collectors.toList()))
        .numberOfRecords(accountingList.size())
        .numberOfColumns(processList.size()+3)
        .build();
  }

  @Override
  public AccountingResponse getAccountingByDatesToCsv(Long idClient, String idBusiness,
      Date startDate, Date endDate) {

    List<AccountingProcessEntity> processList = accountingProcessPort.getProcess(idClient, idBusiness, startDate, endDate);
    List<ToWriteCsv> toWriteCsvList = new ArrayList<>();

    for(AccountingProcessEntity process: processList)
    {

      final List<CuentaContableDimensionsEntity> result = pucPort.getCuentaContableDimensions(idClient, idBusiness, process.getId());
      final List<CuentaContableDimensions> resultOrder = result.stream().map(mapper::entityToDomain).collect(
          Collectors.toList());
      resultOrder.sort(Comparator.comparing(CuentaContableDimensions::getCode));

      toWriteCsvList.add(ToWriteCsv.builder()
          .accountingProcessEntity(process)
          .result(resultOrder)
          .build());
    }

    writeCsv(toWriteCsvList);

    return getAccountingByDates(idClient, idBusiness, startDate, endDate);
  }

  private void writeCsv(List<ToWriteCsv> toWriteCsvList) {
    String outputFilePath = "./data/output_cuenta_contable.csv";

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {

      bw.write("01,date_process,id_client,id_business,id_process");
      bw.newLine();

      bw.write("02,transactional,code,description,initial_balance,debits,credits,final_balance");
      bw.newLine();

      for(ToWriteCsv toWriteCsv: toWriteCsvList)
      {
        bw.write("03," + toWriteCsv.getAccountingProcessEntity().getDateProcess()+","+toWriteCsv.getAccountingProcessEntity().getIdClient()+","+toWriteCsv.getAccountingProcessEntity().getIdBusiness()+","+toWriteCsv.getAccountingProcessEntity().getId());
        bw.newLine();

        for (CuentaContableDimensions cuentaContableDimensions : toWriteCsv.getResult()) {
          bw.write("04" + ",");
          bw.write(cuentaContableDimensions.getTransactional() + ",");
          bw.write(cuentaContableDimensions.getCode() + ",");
          bw.write(cuentaContableDimensions.getDescription().replace(",", " ") + ",");
          bw.write(cuentaContableDimensions.getInitialBalance() + ",");
          bw.write(cuentaContableDimensions.getDebits() + ",");
          bw.write(cuentaContableDimensions.getCredits() + ",");
          bw.write(cuentaContableDimensions.getFinalBalance()+ "");
          bw.newLine();
        }

      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
