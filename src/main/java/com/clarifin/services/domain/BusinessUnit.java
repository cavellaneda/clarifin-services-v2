package com.clarifin.services.domain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessUnit {

  private String id;

  private String name;
  private String externalHostId;
  private String description;
}
