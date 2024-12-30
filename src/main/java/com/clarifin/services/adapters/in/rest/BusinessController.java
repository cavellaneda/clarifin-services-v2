package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.Business;
import com.clarifin.services.domain.Client;
import com.clarifin.services.port.in.BusinessUseCase;
import com.clarifin.services.port.in.ClientUseCase;
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
public class BusinessController {

  @Autowired
  private BusinessUseCase businessUseCase;

  @GetMapping("/client/{idClient}/business")
  public List<Business> getAllBusinessByClient(@PathVariable final Long idClient) {
    return businessUseCase.findAllBusinessByClientId(idClient);
  }

  @PostMapping("/client/{idClient}/business")
  public Business createBusiness(@RequestBody Business business, @PathVariable final Long idClient) {
    return businessUseCase.createBusiness(idClient, business);
  }

  @GetMapping("/client/{idClient}/business/{idBusiness}")
  public ResponseEntity<Business> getBusinessById(@PathVariable final Long idClient, @PathVariable String idBusiness) {
    return businessUseCase.findBusinessById(idBusiness, idClient)
        .map(business -> ResponseEntity.ok().body(business))
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/client/{idClient}/business/{idBusiness}")
  public ResponseEntity<Business> updateBusiness(@PathVariable Long idClient, @PathVariable String idBusiness,
      @RequestBody Business businessUpdate) {
    return businessUseCase.findBusinessById(idBusiness, idClient)
        .map(business -> {
          business.setName(businessUpdate.getName());
          business.setDocument(businessUpdate.getDocument());
          business.setTypeDocument(businessUpdate.getTypeDocument());
          business.setEmail(businessUpdate.getEmail());
          business.setPhone(businessUpdate.getPhone());
          business.setAddress(businessUpdate.getAddress());
          business.setCity(businessUpdate.getCity());
          business.setIndustry(businessUpdate.getIndustry());
          Business updatedBusiness = businessUseCase.saveBusiness(business, idClient);
          return ResponseEntity.ok().body(updatedBusiness);
        }).orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/client/{idClient}/business/{idBusiness}")
  public ResponseEntity<Void> deleteBusiness(@PathVariable Long idClient, @PathVariable String idBusiness) {
    final Optional<Business> business = businessUseCase.findBusinessById(idBusiness, idClient);
    if (business.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      businessUseCase.deleteBusiness(business.get().getId());
      return ResponseEntity.ok().build();
    }
  }

}
