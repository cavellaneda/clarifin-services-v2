package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.BusinessUnitEntity;
import com.clarifin.services.adapters.out.persistence.entities.CompanyEntity;
import com.clarifin.services.domain.BusinessUnit;
import com.clarifin.services.port.out.BusinessUnitPort;
import com.clarifin.services.port.out.CompanyPort;
import com.clarifin.services.services.util.UtilUuid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessUnitAdapter implements BusinessUnitPort {

  @Autowired
  private BusinessUnitRepository businessUnitRepository;

  @Override
  public Optional<BusinessUnitEntity> findByClientAndIdCompanyAndBusinessUnit(Long idClient,
      String idCompany, String idBusinessUnit) {
    return businessUnitRepository.findById(idBusinessUnit);
  }

  @Override
  public List<BusinessUnitEntity> findAllBusinessUnitByCompanyId(String id) {
    return businessUnitRepository.findBusinessUnitEntitiesByIdCompany(id);
  }

  @Override
  public void createBusinessUnitList(String id, List<BusinessUnit> businessUnits) {

    businessUnits.forEach(businessUnit -> {
      final BusinessUnitEntity entityToCreate = BusinessUnitEntity.builder()
          .id(UtilUuid.generateUuid())
          .name(businessUnit.getName())
          .description(businessUnit.getDescription())
          .externalHostId(businessUnit.getExternalHostId())
          .idCompany(id)
          .build();

      businessUnitRepository.save(entityToCreate);
    });
  }

  @Override
  public void deleteBusinessUnitByCompanyId(String idCompany) {
    businessUnitRepository.deleteAllByIdCompany(idCompany);
  }
}
