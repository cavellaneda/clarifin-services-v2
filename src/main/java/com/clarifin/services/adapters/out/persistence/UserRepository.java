package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

  List<UserEntity> findUserEntitiesByIdClient(Long idClient);

  Optional<UserEntity> findUserEntityByIdClientAndId(Long idClient, String idUser);

  Optional<UserEntity> findUserEntityByEmailAndPassword(String email, String password);

}
