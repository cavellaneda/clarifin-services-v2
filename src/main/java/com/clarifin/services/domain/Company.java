package com.clarifin.services.domain;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Company {

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

  private boolean haveBusinessUnit;
  private List<BusinessUnit> businessUnits;

}
