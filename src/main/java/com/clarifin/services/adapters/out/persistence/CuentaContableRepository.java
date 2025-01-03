package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.ClientEntity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CuentaContableRepository extends JpaRepository<CuentaContableEntity, String> {

  void deleteCuentaContableEntitiesByIdProcess(String uuid);

  @Modifying // Indica que esta consulta modifica datos
  @Transactional // Opcional si ya manejas la transacción en otro nivel
  @Query("DELETE FROM CuentaContableEntity e WHERE e.idProcess = :uuid")
  int deleteOldRecords(@Param("uuid") String uuid);

}
