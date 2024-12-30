package com.clarifin.services.adapters.out.persistence.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "AccountValidationOn2View")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountingProcessValidationOn2Entity {

  @Id
  @Column(name = "level_code")
  private String levelCode;

  @Column(name = "total_value_2")
  private Double totalValue2;

  @Column(name = "total_value_1")
  private Double totalValue1;

  @Column(name = "ID_PROCESS")
  private String idProcess;

  @Column(name = "validation_result")
  private String validationResult;
}
