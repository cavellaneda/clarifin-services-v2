package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.AccountingResponse;
import com.clarifin.services.domain.TemplateModel;
import com.clarifin.services.domain.TemplateModelByClient;
import com.clarifin.services.port.in.TemplateModelUseCase;
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
public class TemplateModelController {

  @Autowired
  private TemplateModelUseCase templateModelCase;

  @GetMapping("/template-model")
  public List<TemplateModel> getAllTemplateModel() {
    return templateModelCase.findAllTemplateModel();
  }

  @GetMapping("/template-model/{idTemplateModel}/client/{idClient}/business/{idBusiness}/accounting")
  public List<TemplateModelByClient> getAllTemplateModel(
      @PathVariable final Long idClient, @PathVariable String idBusiness,
      @RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate,
      @PathVariable String idTemplateModel
  ) {
    return templateModelCase.getTemplateModelByClient(idClient, idBusiness,
        UtilDate.convertDate(startDate), UtilDate.convertDate(endDate), idTemplateModel);
  }
}
