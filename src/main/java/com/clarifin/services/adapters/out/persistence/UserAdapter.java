package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.UserEntity;
import com.clarifin.services.port.out.UserPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserPort {

  @Autowired
  private UserRepository userRepository;

  @Override
  public List<UserEntity> findAllUsersByClientId(Long idClient) {
    return userRepository.findUserEntitiesByIdClient(idClient);
  }

  @Override
  public UserEntity createUser(UserEntity entityToCreate) {
    return userRepository.save(entityToCreate);
  }

  @Override
  public Optional<UserEntity> findUserById(Long idClient, String idUser) {
    return userRepository.findUserEntityByIdClientAndId(idClient, idUser);
  }

  @Override
  public UserEntity saveUser(UserEntity entityToUpdate) {
    return userRepository.save(entityToUpdate);
  }

  @Override
  public void deleteUser(String idUser) {
    userRepository.deleteById(idUser);
  }

  @Override
  public Optional<UserEntity> findByEmailAndPassword(String email, String password) {
    return userRepository.findUserEntityByEmailAndPassword(email, password);
  }
}
