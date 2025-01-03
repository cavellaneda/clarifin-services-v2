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
@Table(name = "business_unit_entity")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessUnitEntity {

  @Id
  private String id;

  private String name;
  private String externalHostId;
  private String idCompany;
  private String description;
}
