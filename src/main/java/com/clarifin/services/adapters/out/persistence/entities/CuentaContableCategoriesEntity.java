package com.clarifin.services.adapters.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "cuenta_contable_category_view")
@AllArgsConstructor
@NoArgsConstructor
public class CuentaContableCategoriesEntity {

  @Id
  @Column(name = "id_process")
  private String idProcess;
  private Long code;

  private String transactional;


  @Column(name = "registro_tipo")
  private String registroTipo;


  @Column(name = "category_template")
  private String categoryTemplate;


  @Column(name = "level_entity_id")
  private String idLevel;

  @Column(name = "level_entity_name")
  private String nameLevel;


}
