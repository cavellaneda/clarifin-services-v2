package com.clarifin.services.adapters.out.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class TemplateModelEntity {

  @Id
  private String id;

  private String name;
  private String industry;
  private boolean base;
}
