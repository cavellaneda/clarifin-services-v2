package com.clarifin.services.port.in;

import com.clarifin.services.domain.Business;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface BusinessUseCase {

  List<Business> findAllBusinessByClientId(@NotNull Long idClient);

  Business createBusiness(@NotNull Long idClient, @NotNull Business business);

  Optional<Business> findBusinessById(@NotBlank String idBusiness, @NotNull Long idClient);

  Business saveBusiness(@NotNull Business business, @NotNull Long idClient);

  void deleteBusiness(@NotBlank String idBusiness);
}
