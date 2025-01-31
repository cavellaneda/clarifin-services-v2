package com.clarifin.services.services;

import com.clarifin.services.adapters.out.persistence.CategoriesKeyRepository;
import com.clarifin.services.adapters.out.persistence.CategoriesOn6Repository;
import com.clarifin.services.adapters.out.persistence.CuentaContableCategoriesRepository;
import com.clarifin.services.adapters.out.persistence.TemplateMasterCategoriesRepository;
import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessEntity;
import com.clarifin.services.adapters.out.persistence.entities.BusinessUnitEntity;
import com.clarifin.services.adapters.out.persistence.entities.ClientEntity;
import com.clarifin.services.adapters.out.persistence.entities.CompanyEntity;
import com.clarifin.services.adapters.out.persistence.entities.CategoriesKeyEntity;
import com.clarifin.services.adapters.out.persistence.entities.CategoriesOn6Entity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableDimensionsEntity;
import com.clarifin.services.adapters.out.persistence.entities.TemplateMasterCategoriesKeysEntity;
import com.clarifin.services.domain.Accounting;
import com.clarifin.services.domain.AccountingProcessResponse;
import com.clarifin.services.domain.AccountingResponse;
import com.clarifin.services.domain.BusinessUnit;
import com.clarifin.services.domain.Client;
import com.clarifin.services.domain.CuentaContableDimensions;
import com.clarifin.services.domain.ToWriteCsv;
import com.clarifin.services.domain.mappers.CuentaContableDimensionsMapper;
import com.clarifin.services.port.in.AccountingUseCase;
import com.clarifin.services.port.out.AccountingProcessPort;
import com.clarifin.services.port.out.BusinessUnitPort;
import com.clarifin.services.port.out.ClientPort;
import com.clarifin.services.port.out.CompanyPort;
import com.clarifin.services.port.out.PucPort;
import com.clarifin.services.services.util.UtilUuid;
import com.google.gson.Gson;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class AccountingService implements AccountingUseCase {

  @PersistenceContext
  private EntityManager entityManager;

  final CuentaContableDimensionsMapper mapper = CuentaContableDimensionsMapper.INSTANCE;

  @Autowired
  private PucPort pucPort;

  @Autowired
  private AccountingProcessPort accountingProcessPort;

  @Autowired
  private CompanyPort companyPort;

  @Autowired
  private BusinessUnitPort businessUnitPort;

  @Autowired
  private ClientPort clientPort;


  @Autowired
  private TemplateMasterCategoriesRepository templateMasterCategoriesRepository;

  @Autowired
  private CuentaContableCategoriesRepository cuentaContableCategoriesRepository;

  @Autowired
  private CategoriesKeyRepository categoriesKeyRepository;

  @Autowired
  private CategoriesOn6Repository categoriesOn6Repository;

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  public AccountingService(@Value("${aws.accessKeyId}") String accessKeyId,
      @Value("${aws.secretKey}") String secretKey,
      @Value("${aws.region}") String region) {
    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);
    s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();

    s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }

  public void uploadFile(String key, Path filePath) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    s3Client.putObject(putObjectRequest, filePath);
  }

  public URL generatePresignedUrl(String key) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .getObjectRequest(getObjectRequest)
        .signatureDuration(Duration.ofMinutes(10))
        .build();

    return s3Presigner.presignGetObject(presignRequest).url();
  }

  @Override
  public AccountingResponse getAccountingByDates(Long idClient, String idCompany, Date startDate,
      Date endDate, List<String> businessUnit) {

    List<CuentaContableDimensionsEntity> result = pucPort.getCuentaContableDimensions(idClient, idCompany, startDate, endDate);

    List<AccountingProcessEntity> processList = accountingProcessPort.getProcess(idClient, idCompany, startDate, endDate);

    List<TemplateMasterCategoriesKeysEntity> customByBusiness = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntitiesByIdCompany(idCompany);

    List<BusinessUnitEntity> businessUnitList = businessUnitPort.findAllBusinessUnitByCompanyId(idCompany);

    if (businessUnit != null && !businessUnit.isEmpty()) {
      businessUnitList = businessUnitList.stream().filter(businessUnitEntity -> businessUnit.contains(businessUnitEntity.getExternalHostId())).collect(Collectors.toList());
    }

    List<BusinessUnitEntity> finalBusinessUnitList = businessUnitList;

    result = result.stream().filter(cuentaContableDimensionsEntity -> finalBusinessUnitList.stream().anyMatch(businessUnitEntity -> businessUnitEntity.getId().equals(cuentaContableDimensionsEntity.getIdBusinessUnit()))).collect(Collectors.toList());

    Optional<CompanyEntity> company = companyPort.findByClientAndIdCompany(idClient, idCompany);

    List<TemplateMasterCategoriesKeysEntity> templateBase = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntitiesByTypeIndustryInAndIdCompanyIsNull(List.of("ALL", company.get().getIndustry()));

    final List<String> templateList = new ArrayList<>();

    final List<String> templateIds = new ArrayList<>();
    customByBusiness.forEach(templateMasterCategoriesKeysEntity -> {
      templateIds.add(templateMasterCategoriesKeysEntity.getId() + "-"+ templateMasterCategoriesKeysEntity.getIdBusinessUnit());
    });

    templateList.addAll(customByBusiness.stream().map(TemplateMasterCategoriesKeysEntity::getId).collect(Collectors.toList()));

    final List<String> templateBaseIds = new ArrayList<>();
    if (templateBase.size()>1){
      templateBase.forEach(templateMasterCategoriesEntity -> {
        if(!templateMasterCategoriesEntity.getTypeIndustry().equals("ALL")){
          customByBusiness.add(templateMasterCategoriesEntity);
          templateList.add(templateMasterCategoriesEntity.getId()) ;
          templateBaseIds.add(templateMasterCategoriesEntity.getId() + "-"+ templateMasterCategoriesEntity.getIdBusinessUnit());
        }
      } );
    }
    else{
      templateList.add(templateBase.get(0).getId()) ;
      customByBusiness.addAll(templateBase);
      templateBaseIds.add(templateBase.get(0).getId() + "-"+ templateBase.get(0).getIdBusinessUnit());
    }

    List<CategoriesKeyEntity> categoriesLevelList = categoriesKeyRepository.findCategoriesKeyEntitiesByIdTemplateMasterCategoriesIn(
        customByBusiness.stream().map(TemplateMasterCategoriesKeysEntity::getId).collect(Collectors.toList()));

    List<CategoriesOn6Entity> categoriesOn6EntitiesTotals = new ArrayList<>();

    customByBusiness.forEach(templateMasterCategoriesKeysEntity -> {
      List<CategoriesOn6Entity> categoriesOn6Entities = categoriesOn6Repository.findCategoriesOn6EntitiesByIdTemplateMasterCategories(
          templateMasterCategoriesKeysEntity.getId());
      categoriesOn6EntitiesTotals.addAll(categoriesOn6Entities);
    });

    Map<String, String > categoriesLevel = categoriesLevelList.stream().collect(Collectors.toMap(
        item -> item.getCode().toString() + "-" + item.getIdTemplateMasterCategories() + "-" + item.getIdBusinessUnit(),
        CategoriesKeyEntity::getName,
        (existingValue, newValue) -> existingValue
        ));

    categoriesLevel.putAll(categoriesOn6EntitiesTotals.stream().collect(Collectors.toMap(
        item -> item.getCode().toString() + "-" + item.getIdTemplateMasterCategories() + "-" + item.getIdBusinessUnit(),
        CategoriesOn6Entity::getName,
        (existingValue, newValue) -> existingValue
        )) ) ;


    List<Accounting> accountingList = new ArrayList<>();
    Map<String, Accounting> accountingMap = new HashMap<>();

    int i = 1;

    for (CuentaContableDimensionsEntity entity : result) {

      System.out.println(entity.getCode() + ": " + i);
      i++;

      String code = String.valueOf(entity.getCode());

      final Accounting accounting = accountingMap.getOrDefault(code + "-" + entity.getIdBusinessUnit(), new Accounting());
      accounting.setCode(code);
      accounting.setDescription(entity.getDescription());
      accounting.setTransactional(entity.getTransactional());
      accounting.setBusinessUnit(entity.getIdBusinessUnit());
      accounting.setBusinessUnitExternalHostId(businessUnitList.stream().filter(businessUnitEntity -> businessUnitEntity.getId().equals(entity.getIdBusinessUnit())).findFirst().get().getExternalHostId());
      accounting.setMetadata(entity.getMetadata());

      if ("S".equals(entity.getTransactional())) {
        accounting.setCategory(
            validateCategory(code, categoriesLevel, templateBaseIds, templateList, entity.getIdBusinessUnit()));
      }

      if (accounting.getBalances() == null) {
        accounting.setBalances(new HashMap<>());

        String dateKey = new SimpleDateFormat("yyyy-MM-dd").format(entity.getDateProcess());
        accounting.getBalances().put(dateKey, entity.getFinalBalance());
      }
      else {

        String dateKey = new SimpleDateFormat("yyyy-MM-dd").format(entity.getDateProcess());

        if(accounting.getBalances().containsKey(dateKey)){
          final Double multipleValues = accounting.getBalances().get(dateKey) + entity.getFinalBalance();
          accounting.getBalances().put(dateKey, multipleValues);
        }
        else{
          accounting.getBalances().put(dateKey, entity.getFinalBalance());
        }

      }

      accountingMap.put(code + "-" + entity.getIdBusinessUnit(), accounting);
    }

    accountingList.addAll(accountingMap.values());

    accountingList.sort(Comparator.comparing(Accounting::getBusinessUnit).thenComparing(Accounting::getCode));

    System.out.println("accountingList.size() = " + accountingList.size())  ;

    accountingList.forEach(accounting -> {
      System.out.println(accounting.getCode() + "|" + accounting.getTransactional() + "|" + accounting.getDescription() + "|" + accounting.getCategory() + "|" +accounting.getBusinessUnit() + "|" + accounting.getBusinessUnitExternalHostId() + "|" + accounting.getBalances().size());
    });

    return AccountingResponse.builder()
        .accounting(accountingList)
        .columnsName(processList.stream().map(accountingProcessEntity -> {
          String dateKey = new SimpleDateFormat("yyyy-MM-dd").format(accountingProcessEntity.getDateProcess());
          return dateKey;
        }).collect(Collectors.toList()))
        .numberOfRecords(accountingList.size())
        .numberOfColumns(processList.size()+3)
        .build();
  }

  private String validateCategory(String code, Map<String, String> categoriesLevel, List<String> templateBaseIds,
      List<String> templateCustomIds, String idBusinessUnit) {

    Map<String, String> uncategory = Map.of(
        "1", "Unclassified assets",
        "2", "Unclassified liabilities",
        "3", "Unclassified equity",
        "4", "Unclassified revenue",
        "5", "Unclassified expense",
        "6", "Unclassified cost",
        "7", "Other unclassified income/expense",
        "8", "Other unclassified income/expense",
        "9", "Other unclassified income/expense"
    );

    String category = null ;

    for (String templateId : templateCustomIds) {
      category = categoriesLevel.get(code + "-" + templateId + "-" + idBusinessUnit);
      if(category != null) break;
    }

    if (category == null) {
      category = categoriesLevel.get(code + "-" + templateBaseIds.get(0) + "-null");
      /*if(code.length() <= 4){
        if (category == null) {
          category = uncategory.get(code.substring(0, 1));
        }
      }
      else {
      */
        if (category == null) {
          for (String templateId : templateCustomIds) {
            category = categoriesLevel.get(code.substring(0, 6) + "-" + templateId);
            if(category != null) break;
          }
          if (category == null) {
            category = categoriesLevel.get(code.substring(0, 6) + "-" + templateBaseIds.get(0));
            if (category == null) {
              category = uncategory.get(code.substring(0, 1));
            }
          }
        }
      //}
    }

    return category;
  }

  @Override
  public AccountingResponse getAccountingByDatesToCsv(Long idClient, String idCompany,
      Date startDate, Date endDate, List<String> businessUnit) {

    List<AccountingProcessEntity> processList = accountingProcessPort.getProcess(idClient, idCompany, startDate, endDate);
    List<ToWriteCsv> toWriteCsvList = new ArrayList<>();

    List<BusinessUnitEntity> businessUnitList = businessUnitPort.findAllBusinessUnitByCompanyId(idCompany);

    if (businessUnit != null && !businessUnit.isEmpty()) {
      businessUnitList = businessUnitList.stream().filter(businessUnitEntity -> businessUnit.contains(businessUnitEntity.getExternalHostId())).collect(Collectors.toList());
    }

    List<BusinessUnitEntity> finalBusinessUnitList = businessUnitList;

    for(AccountingProcessEntity process: processList)
    {
      List<CuentaContableDimensionsEntity> result = pucPort.getCuentaContableDimensions(idClient, idCompany, process.getId());

      result = result.stream().filter(cuentaContableDimensionsEntity -> finalBusinessUnitList.stream().anyMatch(businessUnitEntity -> businessUnitEntity.getId().equals(cuentaContableDimensionsEntity.getIdBusinessUnit()))).collect(Collectors.toList());

      final List<CuentaContableDimensions> resultOrder = result.stream().map(mapper::entityToDomain).collect(
          Collectors.toList());
      resultOrder.sort(Comparator.comparing(CuentaContableDimensions::getCode));

      toWriteCsvList.add(ToWriteCsv.builder()
          .accountingProcessEntity(process)
          .result(resultOrder)
          .build());
    }

    List<TemplateMasterCategoriesKeysEntity> customByBusiness = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntitiesByIdCompany(idCompany);

    Optional<CompanyEntity> company = companyPort.findByClientAndIdCompany(idClient, idCompany);

    List<TemplateMasterCategoriesKeysEntity> templateBase = templateMasterCategoriesRepository.findTemplateMasterCategoriesEntitiesByTypeIndustryInAndIdCompanyIsNull(List.of("ALL", company.get().getIndustry()));

    final List<String> templateList = new ArrayList<>();

    final List<String> templateIds = new ArrayList<>();
    customByBusiness.forEach(templateMasterCategoriesKeysEntity -> {
      templateIds.add(templateMasterCategoriesKeysEntity.getId() + "-"+ templateMasterCategoriesKeysEntity.getIdBusinessUnit());
    });

    templateList.addAll(customByBusiness.stream().map(TemplateMasterCategoriesKeysEntity::getId).collect(Collectors.toList()));

    final List<String> templateBaseIds = new ArrayList<>();
    if (templateBase.size()>1){
      templateBase.forEach(templateMasterCategoriesEntity -> {
        if(!templateMasterCategoriesEntity.getTypeIndustry().equals("ALL")){
          customByBusiness.add(templateMasterCategoriesEntity);
          templateList.add(templateMasterCategoriesEntity.getId()) ;
          templateBaseIds.add(templateMasterCategoriesEntity.getId() + "-"+ templateMasterCategoriesEntity.getIdBusinessUnit());
        }
      } );
    }
    else{
      templateList.add(templateBase.get(0).getId()) ;
      customByBusiness.addAll(templateBase);
      templateBaseIds.add(templateBase.get(0).getId() + "-"+ templateBase.get(0).getIdBusinessUnit());
    }

    List<CategoriesKeyEntity> categoriesLevelList = categoriesKeyRepository.findCategoriesKeyEntitiesByIdTemplateMasterCategoriesIn(
        customByBusiness.stream().map(TemplateMasterCategoriesKeysEntity::getId).collect(Collectors.toList()));

    List<CategoriesOn6Entity> categoriesOn6EntitiesTotals = new ArrayList<>();

    customByBusiness.forEach(templateMasterCategoriesKeysEntity -> {
      List<CategoriesOn6Entity> categoriesOn6Entities = categoriesOn6Repository.findCategoriesOn6EntitiesByIdTemplateMasterCategories(
          templateMasterCategoriesKeysEntity.getId());
      categoriesOn6EntitiesTotals.addAll(categoriesOn6Entities);
    });

    Map<String, String > categoriesLevel = categoriesLevelList.stream().collect(Collectors.toMap(
        item -> item.getCode().toString() + "-" + item.getIdTemplateMasterCategories() + "-" + item.getIdBusinessUnit(),
        CategoriesKeyEntity::getName,
        (existingValue, newValue) -> existingValue
    ));

    categoriesLevel.putAll(categoriesOn6EntitiesTotals.stream().collect(Collectors.toMap(
        item -> item.getCode().toString() + "-" + item.getIdTemplateMasterCategories() + "-" + item.getIdBusinessUnit(),
        CategoriesOn6Entity::getName,
        (existingValue, newValue) -> existingValue
    )) ) ;


    final URL presingnedUrl = writeCsv(toWriteCsvList, categoriesLevel, templateBaseIds, templateList, idCompany, finalBusinessUnitList);

    final AccountingResponse accountingResponse = getAccountingByDates(idClient, idCompany, startDate, endDate, List.of());

    accountingResponse.setPresignedUrl(presingnedUrl.toString());
    return accountingResponse;
  }

  @Override
  public List<AccountingProcessResponse> getAccountingProcess(Long idClient,
      List<String> idCompany) {

    Optional<ClientEntity> client = clientPort.findClientById(idClient);

    final List<AccountingProcessEntity> processList = new ArrayList<>();

    if(idCompany != null && !idCompany.isEmpty())
    {
      idCompany.forEach(company -> {
        processList.addAll(accountingProcessPort.getProcess(idClient, company));
      });
    }
    else {
      processList.addAll(accountingProcessPort.getProcess(idClient));
    }


    final List<BusinessUnitEntity> businessUnits = new ArrayList<>();

    if(idCompany != null && !idCompany.isEmpty())
    {
      idCompany.forEach(company -> {
        businessUnits.addAll(businessUnitPort.findAllBusinessUnitByCompanyId(company));
      });
    }
    else {
      companyPort.findAllCompanyByClientId(idClient).forEach(company -> {
        businessUnits.addAll(businessUnitPort.findAllBusinessUnitByCompanyId(company.getId()));
      });
    }

    Map<String, BusinessUnitEntity> businessUnitMap = businessUnits.stream().collect(Collectors.toMap(BusinessUnitEntity::getId, businessUnitEntity -> businessUnitEntity));

    return processList.stream().map(
        accountingProcessEntity -> AccountingProcessResponse.builder()
            .idProcess(accountingProcessEntity.getId())
            .idCompany(accountingProcessEntity.getIdCompany())
            .idClient(String.valueOf(accountingProcessEntity.getIdClient()))
            .createAt(accountingProcessEntity.getCreatedAt().toString())
            .updatedAt(accountingProcessEntity.getUpdatedAt().toString())
            .status(accountingProcessEntity.getStatus())
            .companyName(client.get().getName())
            .businessUnits(buildBusinessUnitList(accountingProcessEntity.getIdBusinessUnit(), businessUnitMap))
            .idFormat(accountingProcessEntity.getIdFormat())
            .dateProcess(accountingProcessEntity.getDateProcess().toString())
            .errors(accountingProcessEntity.getErrorDescription())
            .build()
    ).collect(Collectors.toList());
  }

  private List<BusinessUnit> buildBusinessUnitList(String idBusinessUnit,
      Map<String, BusinessUnitEntity> businessUnitMap) {

    List<String> businessUnitIds = new Gson().fromJson(idBusinessUnit, List.class);



    return businessUnitIds.stream().map(businessUnitMap::get).map(businessUnitEntity -> BusinessUnit.builder()
        .id(businessUnitEntity.getId())
        .name(businessUnitEntity.getName())
        .description(businessUnitEntity.getDescription())
        .externalHostId(businessUnitEntity.getExternalHostId())
        .build()).collect(Collectors.toList());
  }

  private URL writeCsv(List<ToWriteCsv> toWriteCsvList, Map<String, String> categoriesLevel,
      List<String> templateBaseId, List<String> templateCustomId, String idBusiness,
      List<BusinessUnitEntity> finalBusinessUnitList) {
    String outputFilePath = "output_cuenta_contable.csv";

    final List<ToWriteCsv> sorted = toWriteCsvList.stream().sorted(Comparator.comparing((ToWriteCsv toWriteCsv) -> toWriteCsv.getAccountingProcessEntity().getDateProcess()).reversed()).toList();

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath), 819200)) {

      for(ToWriteCsv toWriteCsv: sorted)
      {

        for (CuentaContableDimensions cuentaContableDimensions : toWriteCsv.getResult()) {
          String category = cuentaContableDimensions.getCode().length() > 6
              ? validateCategory(cuentaContableDimensions.getCode(), categoriesLevel, templateBaseId, templateCustomId,
              null)
              : "";

          String businessExternalHostId = finalBusinessUnitList.stream().filter(businessUnitEntity -> businessUnitEntity.getId().equals(cuentaContableDimensions.getIdBusinessUnit())).findFirst().get().getExternalHostId();

          bw.write(String.join(",",
              category,
              cuentaContableDimensions.getTransactional(),
              cuentaContableDimensions.getCode(),
              cuentaContableDimensions.getDescription().replace(",", " "),
              String.valueOf(cuentaContableDimensions.getInitialBalance()),
              String.valueOf(cuentaContableDimensions.getDebits()),
              String.valueOf(cuentaContableDimensions.getCredits()),
              String.valueOf(cuentaContableDimensions.getFinalBalance()),
              toWriteCsv.getAccountingProcessEntity().getDateProcess().toString(),
              String.valueOf(cuentaContableDimensions.getMetadata()),
              businessExternalHostId
          ));
          bw.newLine();
        }

      }

      bw.flush();

      final String path = idBusiness+"/output_cuenta_contable_"+UtilUuid.generateUuid()+".csv";

      uploadFile(path, Path.of(outputFilePath)) ;

      URL presignedUrl = generatePresignedUrl(path);

      Files.deleteIfExists(Path.of(outputFilePath));

      return presignedUrl;

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void writeCsv(List<ToWriteCsv> toWriteCsvList) {
    String outputFilePath = "./data/output_cuenta_contable.csv";

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {

      for(ToWriteCsv toWriteCsv: toWriteCsvList)
      {

        for (CuentaContableDimensions cuentaContableDimensions : toWriteCsv.getResult()) {
          bw.write(cuentaContableDimensions.getCategory() + ",");
          bw.write(cuentaContableDimensions.getTransactional() + ",");
          bw.write(cuentaContableDimensions.getCode() + ",");
          bw.write(cuentaContableDimensions.getDescription().replace(",", " ") + ",");
          bw.write(cuentaContableDimensions.getInitialBalance() + ",");
          bw.write(cuentaContableDimensions.getDebits() + ",");
          bw.write(cuentaContableDimensions.getCredits() + ",");
          bw.write(cuentaContableDimensions.getFinalBalance()+ ",");
          bw.write( toWriteCsv.getAccountingProcessEntity().getDateProcess()+",");
          bw.write( cuentaContableDimensions.getMetadata()+"");
          bw.newLine();
        }

      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
