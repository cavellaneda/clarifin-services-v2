package com.clarifin.services.domain;

import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToWriteCsv {

  private AccountingProcessEntity accountingProcessEntity;
  private List<CuentaContableDimensions> result ;

}
