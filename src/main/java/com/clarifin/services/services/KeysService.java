package com.clarifin.services.services;

import com.clarifin.services.adapters.out.persistence.CategoriesKeysRepository;
import com.clarifin.services.adapters.out.persistence.KeyRepository;
import com.clarifin.services.adapters.out.persistence.entities.BusinessUnitEntity;
import com.clarifin.services.adapters.out.persistence.entities.CompanyEntity;
import com.clarifin.services.adapters.out.persistence.entities.CategoriesKeysEntity;
import com.clarifin.services.adapters.out.persistence.entities.KeyEntity;
import com.clarifin.services.domain.Key;
import com.clarifin.services.domain.mappers.KeyMapper;
import com.clarifin.services.port.in.KeysUseCase;
import com.clarifin.services.port.out.BusinessUnitPort;
import com.clarifin.services.port.out.CompanyPort;
import com.clarifin.services.services.util.UtilUuid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeysService implements KeysUseCase {

  @Autowired
  private KeyRepository keyRepository;


  @Autowired
  private CompanyPort companyPort;

  @Autowired
  private BusinessUnitPort businessUnitPort;

  @Autowired
  private CategoriesKeysRepository categoriesKeysRepository;

  final KeyMapper mapper = KeyMapper.INSTANCE;


  @Override
  public List<Key> getAllKeys() {
    return keyRepository.findKeyEntitiesByIdCompanyIsNull().stream().map(
        mapper::entityToDomain
    ).collect(Collectors.toList());
  }

  @Override
  public List<Key> getKeysByClients(Long idClient, String idCompany) {
    final Optional<CompanyEntity> company = companyPort.findByClientAndIdCompany(idClient,
        idCompany);

    if (company.isEmpty()) {
      throw new RuntimeException("Error: Company not found");
    }

    final List<BusinessUnitEntity> businessUnitList =  businessUnitPort.findAllBusinessUnitByCompanyId(idCompany);

    if(businessUnitList.isEmpty()){
      throw new RuntimeException("Error: Business Unit not found");
    }

    final Map<String, BusinessUnitEntity> businessUnitMap = businessUnitList.stream().collect(
        Collectors.toMap(BusinessUnitEntity::getId, businessUnit -> businessUnit));

    return keyRepository.findKeyEntitiesByIdCompany(idCompany).stream().map( keyEntity -> {
          final Key key =  mapper.entityToDomain(keyEntity);
          key.setBusinessUnitName(businessUnitMap.get(key.getIdBusinessUnit()).getName()) ;
          key.setExternalHostId(businessUnitMap.get(key.getIdBusinessUnit()).getExternalHostId());
          return key;
        }
    ).collect(Collectors.toList());
  }

  @Override
  public void createKeysByClients(Long idClient, String idCompany, List<Key> levelsToClient,
      String idBusinessUnit) {
    final Optional<CompanyEntity> company = companyPort.findByClientAndIdCompany(idClient,
        idCompany);

    if (company.isEmpty()) {
      throw new RuntimeException("Error: Company not found");
    }

    Optional<BusinessUnitEntity> businessUnit = businessUnitPort.findByClientAndIdCompanyAndBusinessUnit(idClient, idCompany, idBusinessUnit);

    if (businessUnit.isEmpty()) {
      throw new RuntimeException("Error: Business Unit not found");
    }
    else{
      if (!businessUnit.get().getIdCompany().equals(idCompany)) {
        throw new RuntimeException("Error: Business Unit not found");
      }
    }

    final List<KeyEntity> entitiesToSave = new ArrayList<>();

    levelsToClient.forEach(level -> {
      KeyEntity keyEntity = mapper.domainToEntity(level);
      keyEntity.setId(UtilUuid.generateUuid());
      keyEntity.setIdCompany(idCompany);
      keyEntity.setIdBusinessUnit(idBusinessUnit);
      entitiesToSave.add(keyEntity);
    });

    keyRepository.saveAll(entitiesToSave);
  }

  @Override
  public void deleteKeyByClients(Long idClient, String idBusiness, Key key, String idBusinessUnit) {
    final Optional<CompanyEntity> company = companyPort.findByClientAndIdCompany(idClient,
        idBusiness);

    if (company.isEmpty()) {
      throw new RuntimeException("Error: Company not found");
    }

    Optional<CategoriesKeysEntity> useKey = categoriesKeysRepository.findCategoriesEntityByIdKey(key.getId());

    if(useKey.isPresent()){
      throw new RuntimeException("Error: Key is in use");
    }

    keyRepository.deleteById(key.getId());
  }
}
