package com.clarifin.services.port.in;

import com.clarifin.services.domain.Client;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface ClientUseCase {

  List<Client> findAllClients();

  Client createClient(@NotNull Client client);

  Optional<Client> findClientById(@NotNull Long id);

  Client saveClient(@NotNull Client client);

  void deleteClient(@NotNull Client client);
}
