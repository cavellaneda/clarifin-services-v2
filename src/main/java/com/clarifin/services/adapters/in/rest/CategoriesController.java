package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.CategoriesClient;
import com.clarifin.services.domain.CodeLevels;
import com.clarifin.services.port.in.AccountingUseCase;
import com.clarifin.services.port.in.CategoriesClientUseCase;
import com.clarifin.services.services.util.UtilDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/entity")
public class CategoriesController {

  @Autowired
  private CategoriesClientUseCase categoriesClientUseCase;

  @GetMapping("/client/{idClient}/business/{idBusiness}/categories")
  public ResponseEntity<List<CategoriesClient>> getCategoriesByClient(@PathVariable final Long idClient, @PathVariable String idBusiness
  ) {
    return ResponseEntity.ok().body(categoriesClientUseCase.getCategoriesByClient(idClient, idBusiness));
  }

  @GetMapping("/categories")
  public ResponseEntity<List<CategoriesClient>> getCategories() {
    return ResponseEntity.ok().body(categoriesClientUseCase.getCategories());
  }

  @PostMapping("/client/{idClient}/business/{idBusiness}/template/{idTemplate}/categories")
  public ResponseEntity<List<String>> postCategoriesByClient(@PathVariable final Long idClient, @PathVariable String idBusiness, @PathVariable String idTemplate, @RequestBody List<CodeLevels> codeLevels
  ) {
    ;
    return ResponseEntity.ok().body(categoriesClientUseCase.postCategoriesByTemplateClient(idClient, idBusiness, idTemplate, codeLevels));
  }

  @PatchMapping("/client/{idClient}/business/{idBusiness}/template/{idTemplate}/categories")
  public ResponseEntity<List<String>> patchCategoriesByClient(@PathVariable final Long idClient, @PathVariable String idBusiness, @PathVariable String idTemplate, @RequestBody List<CodeLevels> codeLevels
  ) {
    ;
    return ResponseEntity.ok().body(categoriesClientUseCase.patchCategoriesByTemplateClient(idClient, idBusiness, idTemplate, codeLevels));
  }



}
