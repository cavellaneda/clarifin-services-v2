package com.clarifin.services.adapters.out.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class CompanyEntity {

  @Id
  private String id;

  private String name;
  private String document;
  private String typeDocument;
  private String email;
  private String phone;
  private String address;
  private String city;
  private String industry;
  private String status;
  private Long idClient;
}
