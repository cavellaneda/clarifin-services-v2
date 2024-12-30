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
public class CuentaContableEntity {

  @Id
  private String id;

  private Long code;
  private String description;
  private Double initialBalance;
  private Double debits;
  private Double credits;
  private Double finalBalance;
  private String transactional;
  private String idProcess;
  private Long idClient;
}
