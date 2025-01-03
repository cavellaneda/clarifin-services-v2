package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.Key;
import com.clarifin.services.port.in.KeysUseCase;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/entity/keys")
public class KeysController {

  @Autowired
  private KeysUseCase keysUseCase;


  @GetMapping("")
  public ResponseEntity<List<Key>> getKeys() {
    return ResponseEntity.ok().body(keysUseCase.getAllKeys());
  }

  @GetMapping("/client/{clientId}/company/{idCompany}")
  public ResponseEntity<List<Key>> getCategoriesByCompany(@PathVariable final Long clientId,
      @PathVariable final String idCompany) {
    return ResponseEntity.ok().body(keysUseCase.getKeysByClients(clientId, idCompany));
  }

  @PostMapping("/client/{clientId}/company/{idCompany}/business_unit/{idBusinessUnit}")
  public ResponseEntity<Void> createCategoriesByCompany(@PathVariable final Long clientId,
      @PathVariable final String idCompany, @PathVariable final String idBusinessUnit, @RequestBody List<Key> levelsToClient) {
    keysUseCase.createKeysByClients(clientId, idCompany, levelsToClient, idBusinessUnit);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/client/{clientId}/company/{idCompany}/business_unit/{idBusinessUnit}")
  public ResponseEntity<Void> deleteCategoriesByCompany(@PathVariable final Long clientId,
      @PathVariable final String idCompany, @PathVariable final String idBusinessUnit, @RequestBody Key key) {
    keysUseCase.deleteKeyByClients(clientId, idCompany, key, idBusinessUnit);
    return ResponseEntity.noContent().build();
  }
}
