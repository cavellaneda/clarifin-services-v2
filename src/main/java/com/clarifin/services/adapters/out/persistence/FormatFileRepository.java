package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.FormatFileEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormatFileRepository  extends JpaRepository<FormatFileEntity, Long> {
  List<FormatFileEntity> findByIdClient(Long idClient);
}
