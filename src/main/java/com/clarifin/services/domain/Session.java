package com.clarifin.services.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Session {

  private String idSession;
  private String idUser;
  private String accessToken;
  private String refreshToken;
}
