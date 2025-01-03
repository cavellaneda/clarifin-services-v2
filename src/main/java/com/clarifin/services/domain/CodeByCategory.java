package com.clarifin.services.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeByCategory {

  private String code;
  private String idLevel;
  private String nameLevel;
  private boolean customByBusiness;
}
