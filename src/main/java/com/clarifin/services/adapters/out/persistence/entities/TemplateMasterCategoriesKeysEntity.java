package com.clarifin.services.adapters.out.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateMasterCategoriesKeysEntity {

  @Id
  private String id;

  private String name;
  private String typeIndustry;
  private String idCompany;
  private String idBusinessUnit;
}
