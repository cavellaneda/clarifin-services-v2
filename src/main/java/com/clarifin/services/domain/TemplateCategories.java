package com.clarifin.services.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TemplateCategories {

  private String id;
  private String name;
  private String industry;
  private String idCompany;
  private String nameCompany;
  private String idBusinessUnit;
  private String nameBusinessUnit;
}
