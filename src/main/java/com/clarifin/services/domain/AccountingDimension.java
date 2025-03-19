package com.clarifin.services.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AccountingDimension {

  private String code;
  private String description;
  private String transactional;
  private String category;
  private Double initialBalance;
  private Double debits;
  private Double credits;
  private Double finalBalance;
  private String dateProcess;
  private String metadata;
  private String businessUnitExternalHostId;
  private String businessUnitName;

}
