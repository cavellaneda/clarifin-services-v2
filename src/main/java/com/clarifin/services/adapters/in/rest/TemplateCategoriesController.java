package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.TemplateCategories;
import com.clarifin.services.port.in.TemplateCategoriesUseCase;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/entity")
public class TemplateCategoriesController {



  @Autowired
  private TemplateCategoriesUseCase templateCategoriesUseCase;


  @PostMapping("/client/{idClient}/company/{idCompany}/business_unit/{idBusinessUnit}/template_categories")
  public ResponseEntity<TemplateCategories> createTemplateCategories(@PathVariable final Long idClient, @PathVariable String idCompany, @PathVariable String idBusinessUnit, @RequestBody TemplateCategories templateCategories) {
    return ResponseEntity.ok().body(templateCategoriesUseCase.create(idClient, idCompany, idBusinessUnit, templateCategories));
  }

  @GetMapping("/client/{idClient}/template_categories")
  public ResponseEntity<List<TemplateCategories>> createTemplateCategories(@PathVariable final Long idClient) {
    return ResponseEntity.ok().body(templateCategoriesUseCase.get(idClient));
  }

}
