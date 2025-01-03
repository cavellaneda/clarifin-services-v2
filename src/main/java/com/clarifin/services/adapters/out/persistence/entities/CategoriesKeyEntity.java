package com.clarifin.services.adapters.out.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "categories_keys_view")
@Entity
public class CategoriesKeyEntity {

  @Id
  private String id;

  private Long code;
  private String idTemplateMasterCategories;
  private String name;
  private String idCompany;
  private String idBusinessUnit;
}
