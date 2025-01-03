package com.clarifin.services.domain;

import com.clarifin.services.adapters.out.persistence.entities.KeyEntity;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TemplateModelConfigByClient {

  private String id;

  private String name;
  private String idBusiness;
  private String idTemplateModel;
  private int order;
  private List<KeyEntity> ruleLevel;
  private String idRecursive;
  private String level;
  private boolean withFormula;
  private String formula;
  private Map<String, Double> balances ;
}
