package com.clarifin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void run(String... args) throws Exception {
    // Drop the view if it already exists
    /*String dropViewSql = "DROP VIEW IF EXISTS AccountValidationOn6View";
    jdbcTemplate.execute(dropViewSql);

    // Create the view
    String createViewSql = "CREATE VIEW AccountValidationOn6View AS " +
        "WITH SumByLength6 AS ( " +
        "SELECT LEFT(CODE, 4) AS level_code, " +
        "SUM(FINAL_BALANCE) AS total_value_6, " +
        "ID_PROCESS " +
        "FROM CUENTA_CONTABLE_ENTITY " +
        "WHERE LENGTH(CODE) = 6 " +
        "GROUP BY LEFT(CODE, 4), ID_PROCESS " +
        "), " +
        "ValueByLength4 AS ( " +
        "SELECT CODE AS level_code, " +
        "FINAL_BALANCE AS total_value_4, " +
        "ID_PROCESS " +
        "FROM CUENTA_CONTABLE_ENTITY " +
        "WHERE LENGTH(CODE) = 4 " +
        "), " +
        "ValidationResults AS ( " +
        "SELECT s.level_code, " +
        "s.total_value_6, " +
        "v.total_value_4, " +
        "s.ID_PROCESS, " +
        "CASE WHEN s.total_value_6 = v.total_value_4 THEN 'MATCH' " +
        "ELSE 'MISMATCH' END AS validation_result " +
        "FROM SumByLength6 s " +
        "LEFT JOIN ValueByLength4 v " +
        "ON s.level_code = v.level_code " +
        "AND s.ID_PROCESS = v.ID_PROCESS " +
        ") " +
        "SELECT level_code, total_value_6, total_value_4, ID_PROCESS, validation_result " +
        "FROM ValidationResults";

    jdbcTemplate.execute(createViewSql);
    */

  }

}
