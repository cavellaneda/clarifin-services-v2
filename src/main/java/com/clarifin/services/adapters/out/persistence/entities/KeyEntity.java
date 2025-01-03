package com.clarifin.services.adapters.out.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "keys_entity")
public class KeyEntity {

  @Id
  private String id;
  private String name;
  private String idCompany;
  private String idBusinessUnit;
}
