package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.ClientEntity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaContableRepository extends JpaRepository<CuentaContableEntity, String> {

  void deleteCuentaContableEntitiesByIdProcess(String uuid);
}
