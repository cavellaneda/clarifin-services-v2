package com.clarifin.services.adapters.out.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "template_config_model_order_view")
public class TemplateModelConfigViewEntity {

  @Id
  private String id;

  private String name;
  private String idBusiness;
  private String idTemplateModel;
  private String orderTemplate;
  private String ruleLevel;
  private String idRecursive;
  private String level;
  private boolean withFormula;
  private String formula;
}
