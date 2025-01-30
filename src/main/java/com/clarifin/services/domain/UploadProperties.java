package com.clarifin.services.domain;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadProperties {

  private Long idClient;
  private String idFormat;
  private Date dateImport;
  private String idCompany;
  private Boolean ignorePreviousBalance;
  private byte[] fileContent;
  private String fileName ;

}
