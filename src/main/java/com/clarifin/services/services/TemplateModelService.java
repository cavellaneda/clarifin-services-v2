package com.clarifin.services.services;

import com.clarifin.services.adapters.out.persistence.KeyRepository;
import com.clarifin.services.adapters.out.persistence.entities.KeyEntity;
import com.clarifin.services.adapters.out.persistence.entities.TemplateModelEntity;
import com.clarifin.services.domain.Accounting;
import com.clarifin.services.domain.AccountingResponse;
import com.clarifin.services.domain.TemplateModel;
import com.clarifin.services.domain.TemplateModelByClient;
import com.clarifin.services.domain.TemplateModelConfigByClient;
import com.clarifin.services.domain.mappers.TemplateModelConfigMapper;
import com.clarifin.services.port.in.AccountingUseCase;
import com.clarifin.services.port.in.TemplateModelUseCase;
import com.clarifin.services.port.out.TemplateModelConfigPort;
import com.clarifin.services.port.out.TemplateModelPort;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateModelService implements TemplateModelUseCase {

  @Autowired
  private TemplateModelConfigPort templateModelConfigPort;

  @Autowired
  private TemplateModelPort templateModelPort;

  @Autowired
  private KeyRepository keyRepository;

  @Autowired
  private AccountingUseCase accountingUseCase;

  final TemplateModelConfigMapper mapper = TemplateModelConfigMapper.INSTANCE;



  @Override
  public List<TemplateModel> findAllTemplateModel() {

    Map<String, KeyEntity> map = new HashMap<>();

    keyRepository.findAll().forEach(levelEntity -> {
      map.put(levelEntity.getId(), levelEntity);
    });

    return templateModelPort.findAllTemplateModelBase().stream().map(templateModelEntity -> {

      TemplateModel templateModel = TemplateModel.builder()
          .base(templateModelEntity.isBase())
          .id(templateModelEntity.getId())
          .name(templateModelEntity.getName())
          .industry(templateModelEntity.getIndustry())
          .templateModelConfigs(new ArrayList<>())
          .build();

      templateModelConfigPort.findTemplateModelConfig(templateModel.getId()).forEach(templateModelConfigViewEntity -> {
        templateModel.getTemplateModelConfigs().add(mapper.entityToDomain(templateModelConfigViewEntity, map));
      });
      return templateModel;
    }).collect(Collectors.toList());
  }

  @Override
  public List<TemplateModelByClient> getTemplateModelByClient(Long idClient, String idBusiness,
      Date startDate, Date endDate, String idTemplateModel) {
    Gson g = new GsonBuilder()
        .registerTypeAdapter(Double.class, new TypeAdapter<Double>() {
          private final DecimalFormat decimalFormat = new DecimalFormat("#.##########");

          @Override
          public void write(JsonWriter out, Double value) throws IOException {
            if (value == null) {
              out.nullValue();
            } else {
              out.value(decimalFormat.format(value));
            }
          }

          @Override
          public Double read(JsonReader in) throws IOException {
            return in.nextDouble();
          }
        })
        .setPrettyPrinting()
        .create();

    Map<String, KeyEntity> map = new HashMap<>();
    Map<String, KeyEntity> mapByName = new HashMap<>();

    List<KeyEntity> levels = keyRepository.findAll();

    levels.forEach(levelEntity -> {
      map.put(levelEntity.getId(), levelEntity);
      mapByName.put(levelEntity.getName(), levelEntity);
    });

    Map<String, Map<String, Double>> balancesByCategory = new HashMap<>();

    final AccountingResponse accountingResponse = accountingUseCase.getAccountingByDates(idClient, idBusiness, startDate, endDate, List.of());

    List<Accounting> accountings = accountingResponse.getAccounting().stream().filter(accounting -> accounting.getTransactional().equals("S")).collect(Collectors.toList());


    accountings.forEach(accounting -> {
      System.out.println(accounting.getCategory());
      System.out.println(accounting.getCode());
      System.out.println(g.toJson(accounting.getBalances()));
      if (balancesByCategory.containsKey(accounting.getCategory())) {
        Map<String, Double> mapResultado = new HashMap<>(balancesByCategory.get(accounting.getCategory()));
        System.out.println(g.toJson(mapResultado));
        accounting.getBalances().forEach((key, value) -> {
          mapResultado.merge(key, value, Double::sum);
        });

        System.out.println(g.toJson(mapResultado));

        balancesByCategory.put(accounting.getCategory(), mapResultado);
      }
      else {
        balancesByCategory.put(accounting.getCategory(), accounting.getBalances());
        System.out.println(g.toJson(accounting.getBalances()));
      }
    });


    TemplateModelEntity templateModelEntity = templateModelPort.findByTemplateModelId(idTemplateModel);

    TemplateModelByClient templateModel = TemplateModelByClient.builder()
        .id(templateModelEntity.getId())
        .name(templateModelEntity.getName())
        .columnsName(accountingResponse.getColumnsName())
        .templateModelConfigs(new ArrayList<>())
        .build();

    templateModelConfigPort.findTemplateModelConfig(templateModel.getId()).forEach(templateModelConfigViewEntity -> {
      TemplateModelConfigByClient templateModelConfigByClient = mapper.entityToDomainByClient(templateModelConfigViewEntity, map);


      Map<String, Double> totalByCategory = new HashMap<>();
      templateModelConfigByClient.getRuleLevel().forEach(levelEntity -> {
        Map<String, Double> total = balancesByCategory.get(levelEntity.getName());
        if (total != null) {
          total.forEach((key, value) -> {
            totalByCategory.merge(key, value, Double::sum);
          });
        }
        //else{
        //  totalByCategory.putAll(total);
        //}
      });

      templateModelConfigByClient.setBalances(totalByCategory);


      templateModel.getTemplateModelConfigs().add(templateModelConfigByClient);
    });

    return List.of(templateModel);
  }

}
