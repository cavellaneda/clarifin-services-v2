package com.clarifin.services.adapters.out.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class TemplateModelConfigEntity {

  @Id
  private String id;

  private String name;
  private String idBusiness;
  private String idTemplateModel;
  private int orderTemplate;
  private String ruleLevel;
  private String idRecursive;
  private boolean withFormula;
  private String formula;
}
