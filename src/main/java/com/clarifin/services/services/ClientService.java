package com.clarifin.services.services;

import com.clarifin.services.domain.Client;
import com.clarifin.services.domain.mappers.ClientMapper;
import com.clarifin.services.port.in.ClientUseCase;
import com.clarifin.services.port.out.ClientPort;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService implements ClientUseCase {

  @Autowired
  private ClientPort clientPort;

  @Override
  public List<Client> findAllClients() {
    return clientPort.findAllClients().stream().map(
        clientEntity -> Client.builder()
            .id(clientEntity.getId())
            .name(clientEntity.getName())
            .email(clientEntity.getEmail())
            .status("ACTIVE")
            .build()
    ).collect(Collectors.toList());
  }

  @Override
  public Client createClient(Client client) {
    return ClientMapper.INSTANCE.entityToDomain(clientPort.createClient(ClientMapper.INSTANCE.domainToEntity(client)));
  }

  @Override
  public Optional<Client> findClientById(Long id) {
    return clientPort.findClientById(id).map(ClientMapper.INSTANCE::entityToDomain);
  }

  @Override
  public Client saveClient(Client client) {
    return ClientMapper.INSTANCE.entityToDomain(clientPort.saveClient(ClientMapper.INSTANCE.domainToEntity(client)));
  }

  @Override
  public void deleteClient(Client client) {
    clientPort.deleteClient(ClientMapper.INSTANCE.domainToEntity(client));
  }
}
