package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.KeyEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyRepository extends
    JpaRepository<KeyEntity, String> {

  List<KeyEntity> findKeyEntitiesByIdCompanyIsNull();

  List<KeyEntity> findKeyEntitiesByIdCompany(String idCompany);
}
