package com.clarifin.services.adapters.out.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories_keys_entity")
public class CategoriesKeysEntity {

  @Id
  private String id;

  private Long code;
  private String idKey;
  private String idTemplateMasterCategories;
}
