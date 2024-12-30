package com.clarifin.services.port.out;

import com.clarifin.services.adapters.out.persistence.entities.ClientEntity;
import com.clarifin.services.domain.Client;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface ClientPort {

  List<ClientEntity> findAllClients();

  ClientEntity createClient(@NotNull ClientEntity client);

  Optional<ClientEntity> findClientById(@NotNull Long id);

  ClientEntity saveClient(@NotNull ClientEntity client);

  void deleteClient(@NotNull ClientEntity client);
}
