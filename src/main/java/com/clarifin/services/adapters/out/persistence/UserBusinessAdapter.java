package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.UserBusinessEntity;
import com.clarifin.services.adapters.out.persistence.entities.UserEntity;
import com.clarifin.services.port.out.UserBusinessPort;
import com.clarifin.services.port.out.UserPort;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBusinessAdapter implements UserBusinessPort {

  @Autowired
  private UserBusinessRepository userBusinessRepository;

  @Override
  public Boolean linkBusinessAndUser(UserBusinessEntity userBusinessEntity) {
    userBusinessEntity = userBusinessRepository.save(userBusinessEntity);
    return Objects.isNull(userBusinessEntity.getId())? Boolean.FALSE : Boolean.TRUE;
  }

  @Override
  public List<UserBusinessEntity> getBusinessByUserId(String idUser) {
    return userBusinessRepository.findUserBusinessEntitiesByIdUser(idUser);
  }

  @Override
  public List<UserBusinessEntity> getUserIdByIdBusiness(String idBusiness) {
    return userBusinessRepository.findUserBusinessEntitiesByIdBusiness(idBusiness);
  }

  @Override
  public List<UserBusinessEntity> getUserBusinessByIdUserAndIdBusiness(String idUser, String idBusiness) {
    return userBusinessRepository.findUserBusinessEntitiesByIdUserAndIdBusiness(idUser, idBusiness);
  }
}
