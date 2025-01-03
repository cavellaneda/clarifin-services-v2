package com.clarifin.services.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Key {

  private String id;
  private String name;
  private String idCompany;
  private String idBusinessUnit;
  private String externalHostId;
  private String businessUnitName;
}
