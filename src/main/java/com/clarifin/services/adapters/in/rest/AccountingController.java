package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.AccountingProcessResponse;
import com.clarifin.services.domain.AccountingResponse;
import com.clarifin.services.port.in.AccountingUseCase;
import com.clarifin.services.services.util.UtilDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/entity")
public class AccountingController {

  @Autowired
  private AccountingUseCase accountingUseCase;

  @GetMapping("/client/{idClient}/company/{idCompany}/accounting")
  public ResponseEntity<AccountingResponse> getAccountingByDates(@PathVariable final Long idClient, @PathVariable String idCompany,
  @RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate, @RequestParam(value = "business_unit", required = false) List<String> businessUnit
  ) {

    return ResponseEntity.ok().body(accountingUseCase.getAccountingByDates(idClient, idCompany,
        UtilDate.convertDate(startDate), UtilDate.convertDate(endDate), businessUnit));
  }

  @GetMapping("/client/{idClient}/company/{idCompany}/accounting/csv")
  public ResponseEntity<AccountingResponse> getAccountingByDatesToCsv(@PathVariable final Long idClient, @PathVariable String idCompany,
      @RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate, @RequestParam(value = "business_unit", required = false) List<String> businessUnit
  ) {
    return ResponseEntity.ok().body(accountingUseCase.getAccountingByDatesToCsv(idClient, idCompany,
        UtilDate.convertDate(startDate), UtilDate.convertDate(endDate), businessUnit));
  }

  @GetMapping("/client/{idClient}/accounting/process")
  public ResponseEntity<List<AccountingProcessResponse>> getAccountingProcess(@PathVariable final Long idClient,
      @RequestParam(value = "id_company", required = false) List<String> idCompany
  ) {
    return ResponseEntity.ok().body(accountingUseCase.getAccountingProcess(idClient, idCompany));
  }
}
