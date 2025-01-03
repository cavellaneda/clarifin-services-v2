package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.CompanyEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {

  List<CompanyEntity> findCompanyEntitiesByIdClient(Long idClient);


  Optional<CompanyEntity> findCompanyEntitiesByIdClientAndId(Long idClient, String idCompany);

}
