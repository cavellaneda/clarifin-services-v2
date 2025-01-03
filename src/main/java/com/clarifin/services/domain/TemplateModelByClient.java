package com.clarifin.services.domain;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateModelByClient {

  private String id;

  private String name;
  private List<String> columnsName;

  private List<TemplateModelConfigByClient> templateModelConfigs;

}
