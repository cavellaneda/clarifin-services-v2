package com.clarifin.services.adapters.out.persistence.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "balance_comparison_result")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BalanceComparisonValidationEntity {

  @Id
  private String code;

  private Double initialBalanceActual;
  private Double finalBalanceAnterior;
  private String validation;
}
