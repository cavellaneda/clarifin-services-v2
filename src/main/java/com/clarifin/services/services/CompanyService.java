package com.clarifin.services.services;

import com.clarifin.services.adapters.out.persistence.entities.CompanyEntity;
import com.clarifin.services.domain.BusinessUnit;
import com.clarifin.services.domain.Company;
import com.clarifin.services.domain.mappers.CompanyMapper;
import com.clarifin.services.port.in.CompanyUseCase;
import com.clarifin.services.port.out.BusinessUnitPort;
import com.clarifin.services.port.out.CompanyPort;
import com.clarifin.services.services.util.UtilUuid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService implements CompanyUseCase {


  public static final String BUSINESS_UNIT_DEFAULT = "BUSINESS UNIT DEFAULT";
  public static final String EXTERNAL_HOST_ID_DEFAULT = "0000";
  final CompanyMapper mapper = CompanyMapper.INSTANCE;

  @Autowired
  private CompanyPort companyPort;

  @Autowired
  private BusinessUnitPort businessUnitPort;

  @Override
  public List<Company> findAllCompanyByClientId(final Long idClient) {
    return companyPort.findAllCompanyByClientId(idClient).stream().map(companyEntity -> {
      final Company company = mapper.entityToDomain(companyEntity);
      company.setBusinessUnits(mapper.businessUnitEntityToDomain(
          businessUnitPort.findAllBusinessUnitByCompanyId(companyEntity.getId())));
      if (!company.getBusinessUnits().isEmpty()) {
        company.setHaveBusinessUnit(true);
      }
      return company;
    }).collect(Collectors.toList());
  }

  @Override
  public Company createCompany(Long idClient, Company company) {
    final CompanyEntity entityToCreate = mapper.domainToEntity(company);
    entityToCreate.setIdClient(idClient);
    entityToCreate.setId(UtilUuid.generateUuid());
    entityToCreate.setStatus("ACTIVE");

    if (company.isHaveBusinessUnit()) {
      businessUnitPort.createBusinessUnitList(entityToCreate.getId(), company.getBusinessUnits());
    }
    else{
      businessUnitPort.createBusinessUnitList(entityToCreate.getId(), List.of(
          BusinessUnit.builder()
              .name(BUSINESS_UNIT_DEFAULT)
              .description(BUSINESS_UNIT_DEFAULT)
              .externalHostId(EXTERNAL_HOST_ID_DEFAULT)
              .build()
      ));
    }
    return mapper.entityToDomain(companyPort.createCompany(entityToCreate));
  }

  @Override
  public Optional<Company> findCompanyById(String idBusiness, Long idClient) {
    return companyPort.findByClientAndIdCompany(idClient, idBusiness).map(companyEntity -> {
      final Company company = mapper.entityToDomain(companyEntity);
      company.setBusinessUnits(mapper.businessUnitEntityToDomain(
          businessUnitPort.findAllBusinessUnitByCompanyId(companyEntity.getId())));
      if (company.getBusinessUnits().isEmpty()) {
        company.setHaveBusinessUnit(true);
      }
      return company;
    });
  }

  @Override
  public Company saveCompany(Company company, Long idClient) {
    final CompanyEntity entityToUpdate = mapper.domainToEntity(company);
    entityToUpdate.setIdClient(idClient);
    return mapper.entityToDomain(companyPort.saveCompany(entityToUpdate));
  }

  @Override
  @Transactional
  public void deleteCompany(String idCompany) {
    businessUnitPort.deleteBusinessUnitByCompanyId(idCompany);
    companyPort.deleteCompany(idCompany);
  }

  @Override
  public void createBusinessUnit(String id, List<BusinessUnit> businessUnitList) {
    businessUnitPort.createBusinessUnitList(id, businessUnitList);
  }
}
