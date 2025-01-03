package com.clarifin.services.domain;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateModel {

  private String id;

  private String name;
  private String industry;
  private boolean base;

  private List<TemplateModelConfig> templateModelConfigs;

}
