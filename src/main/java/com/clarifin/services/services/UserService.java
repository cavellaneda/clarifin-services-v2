package com.clarifin.services.services;

import com.clarifin.services.adapters.out.persistence.entities.UserBusinessEntity;
import com.clarifin.services.adapters.out.persistence.entities.UserEntity;
import com.clarifin.services.domain.Company;
import com.clarifin.services.domain.Login;
import com.clarifin.services.domain.Session;
import com.clarifin.services.domain.User;
import com.clarifin.services.domain.UserComplete;
import com.clarifin.services.domain.mappers.CompanyMapper;
import com.clarifin.services.domain.mappers.UserMapper;
import com.clarifin.services.port.in.UserUseCase;
import com.clarifin.services.port.out.CompanyPort;
import com.clarifin.services.port.out.UserBusinessPort;
import com.clarifin.services.port.out.UserPort;
import com.clarifin.services.services.util.UtilString;
import com.clarifin.services.services.util.UtilUuid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserUseCase {


  final UserMapper mapper = UserMapper.INSTANCE;

  final CompanyMapper companyMapper = CompanyMapper.INSTANCE;

  @Autowired
  private UserPort userPort;

  @Autowired
  private CompanyPort companyPort;

  @Autowired
  private UserBusinessPort userBusinessPort;

  @Override
  public List<User> findAllUsersByClientId(Long idClient) {
    return userPort.findAllUsersByClientId(idClient).stream().map(
        mapper::entityToDomain
    ).collect(Collectors.toList());
  }

  @Override
  public User createUser(Long idClient, UserComplete userComplete) {
    final UserEntity entityToCreate = mapper.domainToEntity(userComplete);
    entityToCreate.setIdClient(idClient);
    entityToCreate.setId(UtilUuid.generateUuid());
    entityToCreate.setStatus("ACTIVE");
    entityToCreate.setPassword(UtilString.generateMd5(userComplete.getPassword()));
    return mapper.entityToDomain(userPort.createUser(entityToCreate));
  }

  @Override
  public Optional<User> findUserById(String idUser, Long idClient) {
    return userPort.findUserById(idClient, idUser).map(mapper::entityToDomain);
  }

  @Override
  public User saveUser(User user, Long idClient) {
    final UserEntity entityToUpdate = mapper.domainToEntity(user);
    entityToUpdate.setIdClient(idClient);
    return mapper.entityToDomain(userPort.saveUser(entityToUpdate));
  }

  @Override
  public void deleteUser(String idUser) {
    userPort.deleteUser(idUser);
  }

  @Override
  public Optional<Session> login(Login login) {
    return userPort.findByEmailAndPassword(login.getEmail(), UtilString.generateMd5(login.getPassword()))
        .map(userEntity ->
          Session.builder()
              .idSession(UtilUuid.generateUuid())
              .idUser(userEntity.getId())
              .accessToken("TBD")
              .refreshToken("TBD")
              .build()
        );
  }

  @Override
  public Boolean linkBusinessAndUser(String idUser, Long idClient, String idBusiness) {
    final Optional<User> user = userPort.findUserById(idClient, idUser).map(mapper::entityToDomain);
    if (user.isEmpty()) {
      return false;
    }

    final Optional<Company> business = companyPort.findByClientAndIdCompany(idClient, idBusiness).map(
        companyMapper::entityToDomain);

    if (business.isEmpty()) {
      return false;
    }

    final UserBusinessEntity userBusinessEntity = UserBusinessEntity.builder()
        .idUser(idUser)
        .idBusiness(idBusiness)
        .build();

    return userBusinessPort.linkBusinessAndUser(userBusinessEntity);

  }
}
