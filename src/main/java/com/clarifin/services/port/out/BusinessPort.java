package com.clarifin.services.port.out;

import com.clarifin.services.adapters.out.persistence.entities.BusinessEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface BusinessPort {

  List<BusinessEntity> findAllBusinessByClientId(@NotNull Long idClient);

  BusinessEntity createBusiness(@NotNull BusinessEntity businessEntity);

  Optional<BusinessEntity> findByClientAndIdBusiness(@NotNull Long idClient, @NotBlank String idBusiness);

  BusinessEntity saveBusiness(@NotNull BusinessEntity businessEntity);

  void deleteBusiness(@NotBlank String idBusiness);
}
