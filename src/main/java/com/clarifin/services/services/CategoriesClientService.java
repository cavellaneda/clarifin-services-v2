package com.clarifin.services.services;

import com.clarifin.services.adapters.out.persistence.CategoriesKeyRepository;
import com.clarifin.services.adapters.out.persistence.CategoriesKeysRepository;
import com.clarifin.services.adapters.out.persistence.KeyRepository;
import com.clarifin.services.adapters.out.persistence.TemplateMasterCategoriesRepository;
import com.clarifin.services.adapters.out.persistence.entities.CompanyEntity;
import com.clarifin.services.adapters.out.persistence.entities.CategoriesKeysEntity;
import com.clarifin.services.adapters.out.persistence.entities.KeyEntity;
import com.clarifin.services.adapters.out.persistence.entities.TemplateMasterCategoriesKeysEntity;
import com.clarifin.services.domain.CategoriesClient;
import com.clarifin.services.domain.CodeByCategory;
import com.clarifin.services.domain.CodeLevels;
import com.clarifin.services.port.in.CategoriesClientUseCase;
import com.clarifin.services.port.out.CompanyPort;
import com.clarifin.services.services.util.UtilUuid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class CategoriesClientService implements CategoriesClientUseCase {

  @Autowired
  private CompanyPort companyPort;

  @Autowired
  private TemplateMasterCategoriesRepository templateMasterCategoriesRepository;

  @Autowired
  private CategoriesKeyRepository categoriesKeyRepository;

  @Autowired
  private KeyRepository keyRepository;

  @Autowired
  private CategoriesKeysRepository categoriesKeysRepository;




  @Override
  public List<CategoriesClient> getCategoriesByClient(Long idClient, String idBusiness) {
    final Optional<CompanyEntity> business = companyPort.findByClientAndIdCompany(idClient,
        idBusiness);

    if (business.isEmpty()) {
      return null;
    }

    final List<TemplateMasterCategoriesKeysEntity> templates = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntitiesByIdCompany(
        idBusiness);

    List<TemplateMasterCategoriesKeysEntity> templatesBase = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntitiesByTypeIndustryInAndIdCompanyIsNull(
        List.of(business.get().getIndustry()));

    if (CollectionUtils.isEmpty(templatesBase)) {
      templatesBase = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntitiesByTypeIndustryInAndIdCompanyIsNull(
          List.of("ALL"));
    }

    final List<TemplateMasterCategoriesKeysEntity> templatesBaseFinal = templatesBase;

    List<CategoriesClient> categoriesClients = new ArrayList<>();

    CategoriesClient categoriesClient = CategoriesClient.builder()
        .id(templatesBaseFinal.get(0).getId()).name(templatesBaseFinal.get(0).getName())
        .industry(templatesBaseFinal.get(0).getTypeIndustry()).build();

    List<CodeByCategory> codeByCategoryList = new ArrayList<>();

    categoriesKeyRepository.findCategoriesKeyEntitiesByIdTemplateMasterCategoriesIn(
        List.of(templatesBaseFinal.get(0).getId())).forEach(categoriesLevelEntity -> {
      codeByCategoryList.add(
          CodeByCategory.builder().code(categoriesLevelEntity.getCode().toString())
              .nameLevel(categoriesLevelEntity.getName()).idLevel(categoriesLevelEntity.getId())
              .build());
    });

    if (!templates.isEmpty()) {
      categoriesClient = CategoriesClient.builder().id(templates.get(0).getId())
          .name(templates.get(0).getName()).industry(templates.get(0).getTypeIndustry()).build();
      categoriesKeyRepository.findCategoriesKeyEntitiesByIdTemplateMasterCategoriesIn(
          List.of(templates.get(0).getId())).forEach(categoriesLevelEntity -> {
        codeByCategoryList.add(
            CodeByCategory.builder().code(categoriesLevelEntity.getCode().toString())
                .nameLevel(categoriesLevelEntity.getName()).idLevel(categoriesLevelEntity.getId())
                .customByBusiness(true).build());
      });
    }

    codeByCategoryList.sort(Comparator.comparing(CodeByCategory::getCode));

    categoriesClient.setCodeByCategoryList(codeByCategoryList);
    categoriesClients.add(categoriesClient);

    return categoriesClients;
  }

  @Override
  public List<CategoriesClient> getCategories() {

    List<TemplateMasterCategoriesKeysEntity> templatesBase = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntitiesByIdCompanyIsNull();

    List<CategoriesClient> categoriesClients = new ArrayList<>();

    templatesBase.forEach(template->{

    CategoriesClient categoriesClient = CategoriesClient.builder()
        .id(template.getId()).name(template.getName())
        .industry(template.getTypeIndustry()).build();

    List<CodeByCategory> codeByCategoryList = new ArrayList<>();

    categoriesKeyRepository.findCategoriesKeyEntitiesByIdTemplateMasterCategoriesIn(
        List.of(template.getId())).forEach(categoriesLevelEntity -> {
      codeByCategoryList.add(
          CodeByCategory.builder().code(categoriesLevelEntity.getCode().toString())
              .nameLevel(categoriesLevelEntity.getName()).idLevel(categoriesLevelEntity.getId())
              .build());
    });

    codeByCategoryList.sort(Comparator.comparing(CodeByCategory::getCode));

    categoriesClient.setCodeByCategoryList(codeByCategoryList);
    categoriesClients.add(categoriesClient);
    });


    return categoriesClients;
  }

  @Override
  public List<String> postCategoriesByTemplateClient(Long idClient, String idBusiness, String idTemplate,
      List<CodeLevels> codeLevels) {

    final Optional<CompanyEntity> business = companyPort.findByClientAndIdCompany(idClient,
        idBusiness);

    if (business.isEmpty()) {
      throw new RuntimeException("Error: Business not found");
    }

    final Optional<TemplateMasterCategoriesKeysEntity> template = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntityByIdAndIdCompany(idTemplate, idBusiness);

    if (template.isEmpty()) {
      throw new RuntimeException("Error: Template not found");
    }

    final List<KeyEntity> levels = keyRepository.findAll();

    Map<String, KeyEntity> levelsMap = levels.stream().collect(Collectors.toMap(KeyEntity::getId, level -> level));

    List<String> result = new ArrayList<>();

    codeLevels.forEach(codeLevel -> {
      if(!levelsMap.containsKey(codeLevel.getIdLevel()))
      {
        result.add("Error: Level not found to code: " + codeLevel.getCode());
      }
      else{
        final CategoriesKeysEntity categories = CategoriesKeysEntity.builder()
            .id(UtilUuid.generateUuid())
            .idTemplateMasterCategories(idTemplate)
            .code(Long.parseLong(codeLevel.getCode()))
            .idKey(codeLevel.getIdLevel())
            .build();

        try{
          categoriesKeysRepository.save(categories);
        } catch (Exception e) {
          e.printStackTrace();
          result.add("Error: Try to save the code: " + codeLevel.getCode());
        }
      }
    });

    return result;
  }

  @Override
  public List<String> patchCategoriesByTemplateClient(Long idClient, String idBusiness,
      String idTemplate, List<CodeLevels> codeLevels) {
    final Optional<CompanyEntity> business = companyPort.findByClientAndIdCompany(idClient,
        idBusiness);

    if (business.isEmpty()) {
      throw new RuntimeException("Error: Business not found");
    }

    final Optional<TemplateMasterCategoriesKeysEntity> template = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntityByIdAndIdCompany(idTemplate, idBusiness);

    if (template.isEmpty()) {
      throw new RuntimeException("Error: Template not found");
    }

    final List<KeyEntity> levels = keyRepository.findAll();

    Map<String, KeyEntity> levelsMap = levels.stream().collect(Collectors.toMap(KeyEntity::getId, level -> level));

    Map<String, CategoriesKeysEntity> categoriesMap = categoriesKeysRepository.findCategoriesEntityByIdTemplateMasterCategories(idTemplate).stream().collect(Collectors.toMap(
        categoriesKeysEntity -> String.valueOf(categoriesKeysEntity.getCode()),
        categoriesKeysEntity -> categoriesKeysEntity
    ));

    List<String> result = new ArrayList<>();


    codeLevels.forEach(codeLevel -> {
      if(!levelsMap.containsKey(codeLevel.getIdLevel()))
      {
        result.add("Error: Level not found to code: " + codeLevel.getCode());
      }
      else{
        CategoriesKeysEntity categories = null;

        if(categoriesMap.containsKey(codeLevel.getCode())){
          if(categoriesMap.get(codeLevel.getCode()).getIdKey().equals(codeLevel.getIdLevel())){
            return;
          }

          categories = categoriesMap.get(codeLevel.getCode());
          categories.setIdKey(codeLevel.getIdLevel());
        }

        if(categories == null){
          categories = CategoriesKeysEntity.builder()
            .id(UtilUuid.generateUuid())
            .idTemplateMasterCategories(idTemplate)
            .code(Long.parseLong(codeLevel.getCode()))
            .idKey(codeLevel.getIdLevel())
            .build();
        }

        try{
          categoriesKeysRepository.save(categories);
        } catch (Exception e) {
          e.printStackTrace();
          result.add("Error: Try to save the code: " + codeLevel.getCode());
        }
      }
    });
    return result;
  }
}
