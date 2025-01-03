package com.clarifin.services.port.out;

import com.clarifin.services.adapters.out.persistence.entities.CompanyEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CompanyPort {

  List<CompanyEntity> findAllCompanyByClientId(@NotNull Long idClient);

  CompanyEntity createCompany(@NotNull CompanyEntity companyEntity);

  Optional<CompanyEntity> findByClientAndIdCompany(@NotNull Long idClient, @NotBlank String idBusiness);

  CompanyEntity saveCompany(@NotNull CompanyEntity companyEntity);

  void deleteCompany(@NotBlank String idBusiness);
}
