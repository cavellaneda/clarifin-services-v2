package com.clarifin.services.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserComplete {

  private String id;

  private String name;
  private String document;
  private String typeDocument;
  private String email;
  private String phone;
  private String address;
  private String city;
  private String status;
  private String password;
}
