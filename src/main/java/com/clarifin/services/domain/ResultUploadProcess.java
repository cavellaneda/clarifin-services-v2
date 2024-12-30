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
public class ResultUploadProcess {

  private String idProcess;
  private String status;
  private String errorDescription;
  private List<String> errors;

}
