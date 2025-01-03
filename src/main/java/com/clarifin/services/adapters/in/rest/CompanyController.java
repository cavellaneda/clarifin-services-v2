package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.BusinessUnit;
import com.clarifin.services.domain.Company;
import com.clarifin.services.port.in.CompanyUseCase;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/entity")
public class CompanyController {

  @Autowired
  private CompanyUseCase companyUseCase;

  @GetMapping("/client/{idClient}/company")
  public List<Company> getAllCompaniesByClient(@PathVariable final Long idClient) {
    return companyUseCase.findAllCompanyByClientId(idClient);
  }

  @PostMapping("/client/{idClient}/company")
  public Company createCompany(@RequestBody Company company, @PathVariable final Long idClient) {
    return companyUseCase.createCompany(idClient, company);
  }

  @GetMapping("/client/{idClient}/company/{idCompany}")
  public ResponseEntity<Company> getCompanyById(@PathVariable final Long idClient, @PathVariable String idCompany) {
    return companyUseCase.findCompanyById(idCompany, idClient)
        .map(business -> ResponseEntity.ok().body(business))
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/client/{idClient}/company/{idCompany}")
  public ResponseEntity<Company> updateCompany(@PathVariable Long idClient, @PathVariable String idCompany,
      @RequestBody Company companyUpdate) {
    return companyUseCase.findCompanyById(idCompany, idClient)
        .map(company -> {
          company.setName(companyUpdate.getName());
          company.setDocument(companyUpdate.getDocument());
          company.setTypeDocument(companyUpdate.getTypeDocument());
          company.setEmail(companyUpdate.getEmail());
          company.setPhone(companyUpdate.getPhone());
          company.setAddress(companyUpdate.getAddress());
          company.setCity(companyUpdate.getCity());
          company.setIndustry(companyUpdate.getIndustry());
          Company updatedCompany = companyUseCase.saveCompany(company, idClient);
          return ResponseEntity.ok().body(updatedCompany);
        }).orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/client/{idClient}/company/{idCompany}")
  public ResponseEntity<Void> deleteCompany(@PathVariable Long idClient, @PathVariable String idCompany) {
    final Optional<Company> business = companyUseCase.findCompanyById(idCompany, idClient);
    if (business.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      companyUseCase.deleteCompany(business.get().getId());
      return ResponseEntity.ok().build();
    }
  }

  @PostMapping("/client/{idClient}/company/{idCompany}")
  public ResponseEntity<Void> createBusinessUnit(@PathVariable Long idClient, @PathVariable String idCompany, @RequestBody List<BusinessUnit> businessUnitList) {
    final Optional<Company> business = companyUseCase.findCompanyById(idCompany, idClient);
    if (business.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      companyUseCase.createBusinessUnit(business.get().getId(), businessUnitList);
      return ResponseEntity.ok().build();
    }
  }
}
