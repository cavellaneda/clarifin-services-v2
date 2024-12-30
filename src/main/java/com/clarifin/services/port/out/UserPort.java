package com.clarifin.services.port.out;

import com.clarifin.services.adapters.out.persistence.entities.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserPort {

  List<UserEntity> findAllUsersByClientId(Long idClient);

  UserEntity createUser(UserEntity entityToCreate);

  Optional<UserEntity> findUserById(Long idClient, String idUser);

  UserEntity saveUser(UserEntity entityToUpdate);

  void deleteUser(String idUser);

  Optional<UserEntity> findByEmailAndPassword(String email, String password);
}
