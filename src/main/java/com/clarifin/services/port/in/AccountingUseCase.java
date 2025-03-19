package com.clarifin.services.port.in;

import com.clarifin.services.domain.AccountingDimension;
import com.clarifin.services.domain.AccountingProcessResponse;
import com.clarifin.services.domain.AccountingResponse;
import java.util.Date;
import java.util.List;

public interface AccountingUseCase {

  AccountingResponse getAccountingByDates(Long idClient, String idBusiness, Date startDate,
      Date endDate, List<String> businessUnit);

  AccountingResponse getAccountingByDatesToCsv(Long idClient, String idBusiness, Date startDate,
      Date endDate, List<String> businessUnit);

  List<AccountingProcessResponse> getAccountingProcess(Long idClient, List<String> idCompany);

  List<AccountingDimension> getAccountingDimensionByDates(Long idClient, String idCompany, Date startDate, Date endDate, List<String> businessUnit, List<String> pucCodes);
}
