package com.clarifin.services.domain;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Accounting {

  private String code;
  private String description;
  private String transactional;
  private String category;

  private Map<String, Double> balances ;
}