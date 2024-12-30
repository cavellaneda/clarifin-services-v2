package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.ClientEntity;
import com.clarifin.services.port.out.ClientPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientAdapter implements ClientPort {

  @Autowired
  private ClientRepository clientRepository;

  @Override
  public List<ClientEntity> findAllClients() {
    return clientRepository.findAll();
  }

  @Override
  public ClientEntity createClient(ClientEntity client) {
    return clientRepository.save(client);
  }

  @Override
  public Optional<ClientEntity> findClientById(Long id) {
    return clientRepository.findById(id);
  }

  @Override
  public ClientEntity saveClient(ClientEntity client) {
    return clientRepository.save(client);
  }

  @Override
  public void deleteClient(ClientEntity client) {
    clientRepository.delete(client);
  }
}
