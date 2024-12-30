package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.Client;
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
@RequestMapping("/v1/entity/client")
public class ClientController {

  @Autowired
  private ClientUseCase clientUseCase;

  @GetMapping
  public List<Client> getAllUsers() {
    return clientUseCase.findAllClients();
  }

  @PostMapping
  public Client createClient(@RequestBody Client client) {
    return clientUseCase.createClient(client);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Client> getUserById(@PathVariable Long id) {
    return clientUseCase.findClientById(id)
        .map(client -> ResponseEntity.ok().body(client))
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Client> updateUser(@PathVariable Long id,
      @RequestBody Client clientDetails) {
    return clientUseCase.findClientById(id)
        .map(client -> {
          client.setName(clientDetails.getName());
          client.setEmail(clientDetails.getEmail());
          Client updatedClient = clientUseCase.saveClient(client);
          return ResponseEntity.ok().body(updatedClient);
        }).orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    final Optional<Client> client = clientUseCase.findClientById(id);
    if (client.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      clientUseCase.deleteClient(client.get());
      return ResponseEntity.ok().build();
    }
  }

}
