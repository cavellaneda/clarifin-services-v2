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
public class AccountingResponse {

  private int numberOfColumns;
  private List<String> columnsName;

  private long numberOfRecords;
  private List<Accounting> accounting;
}
