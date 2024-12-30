package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.AccountingResponse;
import com.clarifin.services.domain.Business;
import com.clarifin.services.port.in.AccountingUseCase;
import com.clarifin.services.port.in.BusinessUseCase;
import com.clarifin.services.services.util.UtilDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/entity")
public class AccountingController {

  @Autowired
  private AccountingUseCase accountingUseCase;

  @GetMapping("/client/{idClient}/business/{idBusiness}/accounting")
  public ResponseEntity<AccountingResponse> getAccountingByDates(@PathVariable final Long idClient, @PathVariable String idBusiness,
  @RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate
  ) {
    return ResponseEntity.ok().body(accountingUseCase.getAccountingByDates(idClient, idBusiness,
        UtilDate.convertDate(startDate), UtilDate.convertDate(endDate)));
  }

  @GetMapping("/client/{idClient}/business/{idBusiness}/accounting/csv")
  public ResponseEntity<AccountingResponse> getAccountingByDatesToCsv(@PathVariable final Long idClient, @PathVariable String idBusiness,
      @RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate
  ) {
    return ResponseEntity.ok().body(accountingUseCase.getAccountingByDatesToCsv(idClient, idBusiness,
        UtilDate.convertDate(startDate), UtilDate.convertDate(endDate)));
  }


}
