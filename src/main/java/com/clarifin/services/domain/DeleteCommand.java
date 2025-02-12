package com.clarifin.services.domain;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeleteCommand {
  private Long idClient;
  private Date dateImport;
  private String idBusiness;
  private String userDelete;

}
