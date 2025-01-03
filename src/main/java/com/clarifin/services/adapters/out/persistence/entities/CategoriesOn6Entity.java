package com.clarifin.services.adapters.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "categories_entity_6_digits")
public class CategoriesOn6Entity {

  @Id
  private String id;

  @Column(name = "code_6_digits")
  private Long code;
  private String idKey;
  private String idTemplateMasterCategories;
  private String name;
  private String idCompany;
  private String idBusinessUnit;

}
