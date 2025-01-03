package com.clarifin.services.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AccountingProcessResponse {

  private String idProcess;
  private String idClient;
  private String createAt;
  private String status;
  private String updatedAt;
  private String idCompany;
  private String companyName;
  private String idFormat;
  private String formatName;
  private List<BusinessUnit> businessUnits;
}
