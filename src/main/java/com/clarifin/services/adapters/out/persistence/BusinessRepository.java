package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.BusinessEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<BusinessEntity, String> {

  List<BusinessEntity> findBusinessEntitiesByIdClient(Long idClient);

  Optional<BusinessEntity> findBusinessEntitiesByIdClientAndId(Long idClient, String idBusiness);

}
