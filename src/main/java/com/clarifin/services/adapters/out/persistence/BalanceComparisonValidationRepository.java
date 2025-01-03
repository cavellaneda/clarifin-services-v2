package com.clarifin.services.adapters.out.persistence;

import com.clarifin.services.adapters.out.persistence.entities.BalanceComparisonValidationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceComparisonValidationRepository extends
    JpaRepository<BalanceComparisonValidationEntity, String> {

  @Query(value = "WITH previous_month AS (" +
      "    SELECT cce.code, " +
      "           cce.id_business_unit, " +
      "           SUM(cce.final_balance) AS final_balance " +
      "    FROM cuenta_contable_entity cce " +
      "    JOIN accounting_process_entity ape ON cce.id_process = ape.id " +
      "    WHERE ape.id = :id_previous " +
      "    GROUP BY cce.code, cce.id_business_unit" +
      "), " +
      "current_month AS (" +
      "    SELECT cce.code, " +
      "           cce.id_business_unit, " +
      "           SUM(cce.initial_balance) AS initial_balance " +
      "    FROM cuenta_contable_entity cce " +
      "    JOIN accounting_process_entity ape ON cce.id_process = ape.id " +
      "    WHERE ape.id = :id_current " +
      "    GROUP BY cce.code, cce.id_business_unit" +
      ") " +
      "SELECT cm.code, " +
      "       cm.id_business_unit, " +
      "       cm.initial_balance AS initial_balance_actual, " +
      "       pm.final_balance AS final_balance_anterior, " +
      "       CASE " +
      "           WHEN cm.initial_balance = pm.final_balance THEN 'Match' " +
      "           ELSE 'Mismatch' " +
      "       END AS validation " +
      "FROM current_month cm " +
      "LEFT JOIN previous_month pm " +
      "ON cm.code = pm.code AND cm.id_business_unit = pm.id_business_unit",
      nativeQuery = true)
  List<BalanceComparisonValidationEntity> findBalanceComparison(@Param("id_previous") String idProcessPrevious, @Param("id_current") String idProcessCurrent);
}
