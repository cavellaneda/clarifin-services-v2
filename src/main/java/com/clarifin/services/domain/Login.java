package com.clarifin.services.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Login {

  private String email;
  private String password;
}
