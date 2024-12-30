package com.clarifin.services.adapters.out.persistence.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuenta_contable_dimensions")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CuentaContableDimensionsEntity {

  @Id
  private String id;
  private String transactional;
  private Long code;
  private String description;
  private Double initialBalance;
  private Double debits;
  private Double credits;
  private Double finalBalance;
  private String idProcess;
  private Date dateProcess;
  private Long idClient;
  private String idBusiness;
}
