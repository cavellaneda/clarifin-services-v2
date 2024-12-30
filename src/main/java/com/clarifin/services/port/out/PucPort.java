package com.clarifin.services.port.out;

import com.clarifin.services.adapters.out.persistence.entities.CuentaContableDimensionsEntity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Validated
public interface PucPort {

  List<CuentaContableEntity> saveCuentasContables(@NotNull List<CuentaContableEntity> cuentasContables);

  void deleteCuentasContables(@NotBlank String uuid);

  List<CuentaContableDimensionsEntity> getCuentaContableDimensions(Long idClient, String idBusiness, Date startDate,
      Date endDate);

  List<CuentaContableDimensionsEntity> getCuentaContableDimensions(Long idClient, String idBusiness, String isProcess);
}
