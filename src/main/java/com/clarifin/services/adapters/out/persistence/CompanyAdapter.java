package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.CompanyEntity;
import com.clarifin.services.port.out.CompanyPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyAdapter implements CompanyPort {

  @Autowired
  private CompanyRepository companyRepository;

  @Override
  public List<CompanyEntity> findAllCompanyByClientId(final Long idClient) {
    return companyRepository.findCompanyEntitiesByIdClient(idClient);
  }

  @Override
  public CompanyEntity createCompany(final CompanyEntity companyEntity) {
    return companyRepository.save(companyEntity);
  }

  @Override
  public Optional<CompanyEntity> findByClientAndIdCompany(final Long idClient,
      final String idBusiness) {
    return companyRepository.findCompanyEntitiesByIdClientAndId(idClient, idBusiness);
  }

  @Override
  public CompanyEntity saveCompany(CompanyEntity companyEntity) {
    return companyRepository.save(companyEntity);
  }

  @Override
  public void deleteCompany(String idBusiness) {
    companyRepository.deleteById(idBusiness);
  }
}
