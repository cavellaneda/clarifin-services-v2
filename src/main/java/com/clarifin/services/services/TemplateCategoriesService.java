package com.clarifin.services.services;

import com.clarifin.services.adapters.out.persistence.TemplateMasterCategoriesRepository;
import com.clarifin.services.adapters.out.persistence.entities.BusinessUnitEntity;
import com.clarifin.services.adapters.out.persistence.entities.CompanyEntity;
import com.clarifin.services.adapters.out.persistence.entities.TemplateMasterCategoriesKeysEntity;
import com.clarifin.services.domain.TemplateCategories;
import com.clarifin.services.domain.mappers.TemplateCategoriesMapper;
import com.clarifin.services.port.in.TemplateCategoriesUseCase;
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
public class TemplateCategoriesService implements TemplateCategoriesUseCase {

  @Autowired
  private TemplateMasterCategoriesRepository templateMasterCategoriesRepository;

  @Autowired
  private CompanyPort companyPort;

  @Autowired
  private BusinessUnitPort businessUnitPort;

  final TemplateCategoriesMapper mapper = TemplateCategoriesMapper.INSTANCE;
      ;

  @Override
  public TemplateCategories create(Long idClient, String idCompany,
      String idBusinessUnit, TemplateCategories templateCategories) {

    final Optional<CompanyEntity> company = companyPort.findByClientAndIdCompany(idClient,
        idCompany);

    if (company.isEmpty()) {
      throw new RuntimeException("Error: Business not found");
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

    final List<TemplateMasterCategoriesKeysEntity> templates = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntitiesByIdCompanyAndIdBusinessUnit(
        idCompany, idBusinessUnit);

    if (!templates.isEmpty()) {
      throw new RuntimeException("Only one template category is allowed by business unit and company");
    }

    TemplateMasterCategoriesKeysEntity templateMasterCategoriesKeysEntity = TemplateMasterCategoriesKeysEntity.builder()
        .id(UtilUuid.generateUuid())
        .idCompany(idCompany)
        .name(templateCategories.getName())
        .typeIndustry(templateCategories.getIndustry())
        .idBusinessUnit(idBusinessUnit)
        .build();

    templateMasterCategoriesRepository.save(templateMasterCategoriesKeysEntity);

    templateCategories.setId(templateMasterCategoriesKeysEntity.getId());
    templateCategories.setIdCompany(templateMasterCategoriesKeysEntity.getIdCompany());
    templateCategories.setNameCompany(company.get().getName());
    templateCategories.setIdBusinessUnit(templateMasterCategoriesKeysEntity.getIdBusinessUnit());
    templateCategories.setNameBusinessUnit(businessUnit.get().getName()) ;

    return templateCategories;
  }

  @Override
  public List<TemplateCategories> get(Long idClient) {

    final List<CompanyEntity> companyList = companyPort.findAllCompanyByClientId(idClient);

    if (companyList.isEmpty()) {
      throw new RuntimeException("Error: Business not found");
    }

    List<BusinessUnitEntity> businessUnitList = new ArrayList<>();

    companyList.forEach(company -> {
      businessUnitList.addAll(businessUnitPort.findAllBusinessUnitByCompanyId(company.getId()));
    });

    if(businessUnitList.isEmpty()){
      throw new RuntimeException("Error: Business Unit not found");
    }

    final Map<String, BusinessUnitEntity> businessUnitMap = businessUnitList.stream().collect(
        Collectors.toMap(BusinessUnitEntity::getId, businessUnit -> businessUnit));

    final List<TemplateMasterCategoriesKeysEntity> templates = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntitiesByIdCompanyIn(
        companyList.stream().map(CompanyEntity::getId).collect(Collectors.toList()));

    return templates.stream().map(mapper::entityToDomain).peek(templateCategories -> {
      CompanyEntity businessAux = companyList.stream().filter(b -> b.getId().equals(templateCategories.getIdCompany())).findFirst().get();
      templateCategories.setNameCompany(businessAux.getName());
      templateCategories.setIdCompany(businessAux.getId());
      templateCategories.setNameBusinessUnit(businessUnitMap.get(templateCategories.getIdBusinessUnit()).getName());
    }).collect(Collectors.toList());

  }
}
