package com.clarifin.services.port.in;

import com.clarifin.services.domain.Key;
import java.util.List;

public interface KeysUseCase {

  List<Key> getAllKeys();

  List<Key> getKeysByClients(Long clientId, String businessId);

  void createKeysByClients(Long idClient, String businessId, List<Key> levelsToClient,
      String idBusinessUnit);

  void deleteKeyByClients(Long idClient, String idBusiness, Key key, String idBusinessUnit);
}
