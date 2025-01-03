package com.clarifin.services.adapters.out.persistence.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactional_confirmation_view")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionalConfirmationEntity {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "code")
  private Long code;

  @Column(name = "id_process")
  private String idProcess;

  @Column(name = "id_business_unit")
  private String idBusinessUnit;

  @Column(name = "transactional")
  private String transactional;
}
