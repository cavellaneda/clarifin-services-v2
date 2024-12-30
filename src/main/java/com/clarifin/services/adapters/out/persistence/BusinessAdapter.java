package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.BusinessEntity;
import com.clarifin.services.port.out.BusinessPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessAdapter implements BusinessPort {

  @Autowired
  private BusinessRepository businessRepository;

  @Override
  public List<BusinessEntity> findAllBusinessByClientId(final Long idClient) {
    return businessRepository.findBusinessEntitiesByIdClient(idClient);
  }

  @Override
  public BusinessEntity createBusiness(final BusinessEntity businessEntity) {
    return businessRepository.save(businessEntity);
  }

  @Override
  public Optional<BusinessEntity> findByClientAndIdBusiness(final Long idClient,
      final String idBusiness) {
    return businessRepository.findBusinessEntitiesByIdClientAndId(idClient, idBusiness);
  }

  @Override
  public BusinessEntity saveBusiness(BusinessEntity businessEntity) {
    return businessRepository.save(businessEntity);
  }

  @Override
  public void deleteBusiness(String idBusiness) {
    businessRepository.deleteById(idBusiness);
  }
}
