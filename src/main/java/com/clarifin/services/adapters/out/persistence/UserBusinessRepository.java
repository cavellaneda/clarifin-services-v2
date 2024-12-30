package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.UserBusinessEntity;
import com.clarifin.services.adapters.out.persistence.entities.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBusinessRepository extends JpaRepository<UserBusinessEntity, Long> {

  List<UserBusinessEntity> findUserBusinessEntitiesByIdUserAndIdBusiness(String idUser, String idBusiness);

  List<UserBusinessEntity> findUserBusinessEntitiesByIdUser(String idUser);

  List<UserBusinessEntity> findUserBusinessEntitiesByIdBusiness(String idBusiness);

}
