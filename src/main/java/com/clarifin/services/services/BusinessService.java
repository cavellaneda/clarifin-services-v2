package com.clarifin.services.services;

import com.clarifin.services.adapters.out.persistence.entities.BusinessEntity;
import com.clarifin.services.domain.Business;
import com.clarifin.services.domain.mappers.BusinessMapper;
import com.clarifin.services.port.in.BusinessUseCase;
import com.clarifin.services.port.out.BusinessPort;
import com.clarifin.services.services.util.UtilUuid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessService implements BusinessUseCase {


  final BusinessMapper mapper = BusinessMapper.INSTANCE;
  @Autowired
  private BusinessPort businessPort;

  @Override
  public List<Business> findAllBusinessByClientId(final Long idClient) {
    return businessPort.findAllBusinessByClientId(idClient).stream().map(
        mapper::entityToDomain
    ).collect(Collectors.toList());
  }

  @Override
  public Business createBusiness(Long idClient, Business business) {
    final BusinessEntity entityToCreate = mapper.domainToEntity(business);
    entityToCreate.setIdClient(idClient);
    entityToCreate.setId(UtilUuid.generateUuid());
    entityToCreate.setStatus("ACTIVE");
    return mapper.entityToDomain(businessPort.createBusiness(entityToCreate));
  }

  @Override
  public Optional<Business> findBusinessById(String idBusiness, Long idClient) {
    return businessPort.findByClientAndIdBusiness(idClient, idBusiness).map(mapper::entityToDomain);
  }

  @Override
  public Business saveBusiness(Business business, Long idClient) {
    final BusinessEntity entityToUpdate = mapper.domainToEntity(business);
    entityToUpdate.setIdClient(idClient);
    return mapper.entityToDomain(businessPort.saveBusiness(entityToUpdate));
  }

  @Override
  public void deleteBusiness(String idBusiness) {
    businessPort.deleteBusiness(idBusiness);
  }
}
