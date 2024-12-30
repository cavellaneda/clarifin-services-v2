


CREATE VIEW account_validation_on6view AS
-- Sumar los valores de los códigos de longitud 6
WITH SumByLength6 AS (
    SELECT
        LEFT(code, 4) AS level_code,
        ROUND(SUM(final_balance),2) AS total_value_6,
        id_process
        FROM cuenta_contable_entity
        WHERE LENGTH(code) = 6
        GROUP BY LEFT(code, 4), id_process
        ),
-- Obtener los valores de los códigos de longitud 4
        ValueByLength4 AS (
        SELECT
        code AS level_code,
        ROUND(final_balance,2) AS total_value_4,
        id_process
        FROM cuenta_contable_entity
        WHERE LENGTH(code) = 4
        ),
-- Comparar ambas sumas
        ValidationResultsOn6 AS (
        SELECT
        s.level_code,
        s.total_value_6,
        v.total_value_4,
        s.id_process,
        CASE
        WHEN s.total_value_6 = v.total_value_4 THEN 'MATCH'
        ELSE 'MISMATCH'
        END AS validation_result
        FROM
        SumByLength6 s
        LEFT JOIN
        ValueByLength4 v
        ON
        s.level_code = v.level_code
        AND s.id_process = v.id_process
        )
SELECT
    level_code,
    total_value_6,
    total_value_4,
    id_process,
    validation_result
FROM
    ValidationResultsOn6;

CREATE VIEW account_validation_on8view AS
-- Sumar los valores de los códigos de longitud 8
WITH SumByLength8 AS (
    SELECT
        LEFT(code, 6) AS level_code,
        ROUND(SUM(final_balance),2) AS total_value_8,
        id_process
        FROM cuenta_contable_entity
        WHERE LENGTH(code) = 8
        GROUP BY LEFT(code, 6), id_process
        ),
-- Obtener los valores de los códigos de longitud 6
        ValueByLength6 AS (
        SELECT
        code AS level_code,
        ROUND(final_balance,2) AS total_value_6,
        id_process
        FROM cuenta_contable_entity
        WHERE LENGTH(code) = 6
        ),
-- Comparar ambas sumas
        ValidationResultsOn8 AS (
        SELECT
        s.level_code,
        s.total_value_8,
        v.total_value_6,
        s.id_process,
        CASE
        WHEN s.total_value_8 = v.total_value_6 THEN 'MATCH'
        ELSE 'MISMATCH'
        END AS validation_result
        FROM
        SumByLength8 s
        LEFT JOIN
        ValueByLength6 v
        ON
        s.level_code = v.level_code
        AND s.id_process = v.id_process
        )
SELECT
    level_code,
    total_value_8,
    total_value_6,
    id_process,
    validation_result
FROM
    ValidationResultsOn8;


CREATE VIEW account_validation_on4view AS
-- Sumar los valores de los códigos de longitud 4
WITH SumByLength4 AS (
    SELECT
        LEFT(code, 2) AS level_code,
        ROUND(SUM(final_balance),2) AS total_value_4,
        id_process
        FROM cuenta_contable_entity
        WHERE LENGTH(code) = 4
        GROUP BY LEFT(code, 2), id_process
        ),
-- Obtener los valores de los códigos de longitud 2
        ValueByLength2 AS (
        SELECT
        code AS level_code,
        ROUND(final_balance,2) AS total_value_2,
        id_process
        FROM cuenta_contable_entity
        WHERE LENGTH(code) = 2
        ),
-- Comparar ambas sumas
        ValidationResultsOn4 AS (
        SELECT
        s.level_code,
        s.total_value_4,
        v.total_value_2,
        s.id_process,
        CASE
        WHEN s.total_value_4 = v.total_value_2 THEN 'MATCH'
        ELSE 'MISMATCH'
        END AS validation_result
        FROM
        SumByLength4 s
        LEFT JOIN
        ValueByLength2 v
        ON
        s.level_code = v.level_code
        AND s.id_process = v.id_process
        )
SELECT
    level_code,
    total_value_4,
    total_value_2,
    id_process,
    validation_result
FROM
    ValidationResultsOn4;


CREATE VIEW account_validation_on2view AS
-- Sumar los valores de los códigos de longitud 2
WITH SumByLength2 AS (
    SELECT
        LEFT(code, 1) AS level_code,
        ROUND(SUM(final_balance),2) AS total_value_2,
        id_process
        FROM cuenta_contable_entity
        WHERE LENGTH(code) = 2
        GROUP BY LEFT(code, 1), id_process
        ),
-- Obtener los valores de los códigos de longitud 1
        ValueByLength1 AS (
        SELECT
        code AS level_code,
        ROUND(final_balance,2) AS total_value_1,
        id_process
        FROM cuenta_contable_entity
        WHERE LENGTH(code) = 1
        ),
-- Comparar ambas sumas
        ValidationResultsOn2 AS (
        SELECT
        s.level_code,
        s.total_value_2,
        v.total_value_1,
        s.id_process,
        CASE
        WHEN s.total_value_2 = v.total_value_1 THEN 'MATCH'
        ELSE 'MISMATCH'
        END AS validation_result
        FROM
        SumByLength2 s
        LEFT JOIN
        ValueByLength1 v
        ON
        s.level_code = v.level_code
        AND s.id_process = v.id_process
        )
SELECT
    level_code,
    total_value_2,
    total_value_1,
    id_process,
    validation_result
FROM
    ValidationResultsOn2;

CREATE VIEW transactional_confirmation_view AS
WITH RecursiveCodes AS (
    SELECT DISTINCT
        id,
        code,
        id_process
    FROM cuenta_contable_entity
),
     TxCheck AS (
         SELECT
             id,
             code,
             id_process,
             CASE
                 WHEN EXISTS (
                     SELECT 1
                     FROM RecursiveCodes AS subcodes
                     WHERE LEFT(subcodes.code, LENGTH(RecursiveCodes.code)) = RecursiveCodes.code
                         AND LENGTH(subcodes.code) > LENGTH(RecursiveCodes.code)
                         AND subcodes.id_process = RecursiveCodes.id_process
                 ) THEN 'N'
                 ELSE 'S'
                 END AS transactional
         FROM RecursiveCodes
     )
SELECT
    id,
    code,
    id_process,
    transactional
FROM
    TxCheck
ORDER BY
    id_process, LENGTH(code) ASC, code;


CREATE VIEW cuenta_contable_dimensions AS
SELECT
    cce.id,
    cce.transactional,
    cce.code,
    cce.description,
    cce.initial_balance,
    cce.debits,
    cce.credits,
    cce.final_balance,
    cce.id_process,
    ape2.date_process,
    ape2.id_client,
    ape2.id_business
FROM cuenta_contable_entity cce
         JOIN accounting_process_entity ape2 ON cce.id_process = ape2.id
WHERE ape2.id IN (
    SELECT ape.id
    FROM accounting_process_entity ape
    WHERE ape.status = 'SUCCESS'
)
Order by code, date_process;