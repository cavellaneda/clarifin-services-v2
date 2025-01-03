package com.clarifin.services.port.in;

import com.clarifin.services.domain.BusinessUnit;
import com.clarifin.services.domain.Company;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CompanyUseCase {

  List<Company> findAllCompanyByClientId(@NotNull Long idClient);

  Company createCompany(@NotNull Long idClient, @NotNull Company company);

  Optional<Company> findCompanyById(@NotBlank String idBusiness, @NotNull Long idClient);

  Company saveCompany(@NotNull Company company, @NotNull Long idClient);

  void deleteCompany(@NotBlank String idBusiness);

  void createBusinessUnit(String id, List<BusinessUnit> businessUnitList);
}
