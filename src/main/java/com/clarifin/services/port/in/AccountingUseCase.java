package com.clarifin.services.port.in;

import com.clarifin.services.domain.AccountingResponse;
import java.util.Date;

public interface AccountingUseCase {

  AccountingResponse getAccountingByDates(Long idClient, String idBusiness, Date startDate,
      Date endDate);

  AccountingResponse getAccountingByDatesToCsv(Long idClient, String idBusiness, Date startDate,
      Date endDate);
}
