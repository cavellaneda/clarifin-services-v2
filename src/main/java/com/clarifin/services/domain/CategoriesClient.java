package com.clarifin.services.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriesClient {

  private String id;
  private String name;
  private String industry;

  private List<CodeByCategory> codeByCategoryList;
}
