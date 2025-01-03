


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
    cce.id_business_unit,
    ape2.date_process,
    ape2.id_client,
    ape2.id_company
FROM cuenta_contable_entity cce
         JOIN accounting_process_entity ape2 ON cce.id_process = ape2.id
WHERE ape2.id IN (
    SELECT ape.id
    FROM accounting_process_entity ape
    WHERE ape.status = 'SUCCESS'
)
Order by code, date_process;


INSERT INTO level_entity VALUES ('d1993564-cf6d-43d8-84a2-9adaf68105e5', 'Cash');
INSERT INTO level_entity VALUES ('6bfc30d1-7122-4d7f-9e50-8fb9b87dd687', 'Business accounts receivable');
INSERT INTO level_entity VALUES ('dd2b8a3d-582a-45bd-b478-3760fd311192', 'Employee accounts receivable');
INSERT INTO level_entity VALUES ('a9cae1a9-2f04-465c-8e99-7d875ccaf392', 'Inventory');
INSERT INTO level_entity VALUES ('ef27374a-97ea-4273-83c9-5e2f53254311', 'PP&E');
INSERT INTO level_entity VALUES ('8ea420ff-ce7d-49b3-87a2-141b37bd5f45', 'Intangible assets');
INSERT INTO level_entity VALUES ('a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba', 'Tax assets');
INSERT INTO level_entity VALUES ('923f343d-074a-4493-8906-334f47670669', 'Suppliers advance');
INSERT INTO level_entity VALUES ('793f826b-8847-4860-9ed0-198e50ea55b7', 'Other assets');
INSERT INTO level_entity VALUES ('455e536c-0ae1-4b46-b9e4-ca8a76f3ba42', 'LT Financial obligations');
INSERT INTO level_entity VALUES ('fa576cfd-264a-4b77-8488-6dc475f521e3', 'ST financial obligations');
INSERT INTO level_entity VALUES ('edf43379-4387-44a7-a761-7d0cb4f38b66', 'Suppliers accounts payable');
INSERT INTO level_entity VALUES ('739ed474-b50e-4ef2-b1ca-3e7ab689d992', 'Related parties accounts payable');
INSERT INTO level_entity VALUES ('86ad7335-84eb-402f-a1a1-4384298442da', 'Employee accounts payable');
INSERT INTO level_entity VALUES ('100b7713-4a7b-49b7-8cb0-c98a9cea914d', 'Business accounts payable');
INSERT INTO level_entity VALUES ('a12a4bed-7c99-4e8f-b61b-594c84ddb357', 'Other accounts payable');
INSERT INTO level_entity VALUES ('9da10948-1ed5-4ee6-acc1-5c958f79e6b1', 'Tax liabilities');
INSERT INTO level_entity VALUES ('529f69d1-4e9b-46dd-aa29-0a1c4c4d36f0', 'Other liabilities');
INSERT INTO level_entity VALUES ('631e20cd-35b3-45e7-8349-885bcd175a7b', 'Capital');
INSERT INTO level_entity VALUES ('18bafb6a-6a21-4df8-97da-1a6850df32ac', 'Accumulated profit/loss');
INSERT INTO level_entity VALUES ('db31a741-4c13-4713-b9c7-0a16ce6b9bda', 'Non operating income/expense');
INSERT INTO level_entity VALUES ('296e0b8d-d6e6-4f95-b49c-39c9cbc23195', 'Operating tax');
INSERT INTO level_entity VALUES ('fcc48fd7-c853-4d65-a042-806cf2c9eac5', 'Other expense');
INSERT INTO level_entity VALUES ('865a24b5-0980-4f4f-962c-ea487dfe685f', 'Discounts');
INSERT INTO level_entity VALUES ('4d51bcb1-5537-447f-b5f1-85d6bcb44d65', 'Maintenance');
INSERT INTO level_entity VALUES ('4364d59b-2a49-4629-a6ff-1daffbbab541', 'Shrinkage');
INSERT INTO level_entity VALUES ('d2127179-b1b0-4b02-8cdd-ec9185e6c942', 'Payroll');
INSERT INTO level_entity VALUES ('400e815d-5d6f-4463-81a4-673518dd4ad0', 'Marketing expense');
INSERT INTO level_entity VALUES ('4244ed4b-80dc-4005-84a2-1180ef8c7cba', 'Employee supplies');
INSERT INTO level_entity VALUES ('ca6ece8d-01e0-44a5-9184-c658693ede34', 'Consulting fees');
INSERT INTO level_entity VALUES ('a88558e9-d650-433b-a775-fdc328316bfe', 'Rent');
INSERT INTO level_entity VALUES ('802b1269-2fc1-4f93-a6b4-5d22c9f6ede6', 'Utilities');
INSERT INTO level_entity VALUES ('09b2be89-54a9-4885-919c-6581823e3277', 'Licenses cost');
INSERT INTO level_entity VALUES ('4776474d-cb1e-4b05-b86e-388f8a421acd', 'D&A expense');
INSERT INTO level_entity VALUES ('f9f689ec-3507-4546-ac51-39eac93b8337', 'Equipment renting');
INSERT INTO level_entity VALUES ('4445fa4e-cd73-4b96-a556-5b2217e64c7d', 'Aggregators fee');
INSERT INTO level_entity VALUES ('1e3573a7-3fda-4dbe-8859-7e23b278fcc4', 'Bank commissions');
INSERT INTO level_entity VALUES ('9fef8445-9e97-4eec-85ec-0d2f5929df2b', 'Financial expense');
INSERT INTO level_entity VALUES ('831c1ed7-585a-4437-849d-afbb755820ac', 'COGS');

INSERT INTO categories_entity VALUES ('d824f2b3-3e4d-4032-9b0e-63fa6ed016e7',	36100501	,'18bafb6a-6a21-4df8-97da-1a6850df32ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3e63554e-34b2-4aa0-9fb7-d1d095bfa06c',	37100501	,'18bafb6a-6a21-4df8-97da-1a6850df32ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5d426b89-7194-412f-a9c7-18451121a613',	39999999	,'18bafb6a-6a21-4df8-97da-1a6850df32ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('998d720a-075c-492c-835d-5078541d1e01',	52359507	,'4445fa4e-cd73-4b96-a556-5b2217e64c7d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c315cd7e-a3a5-486f-9161-a0e591172ed5',	53050501	,'1e3573a7-3fda-4dbe-8859-7e23b278fcc4','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('86bec9c6-ba01-4370-a993-8a2c07d421ea',	53050502	,'1e3573a7-3fda-4dbe-8859-7e23b278fcc4','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('26efc901-1732-4727-8d16-7c8ba996ca6d',	53050503	,'1e3573a7-3fda-4dbe-8859-7e23b278fcc4','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a281d913-1868-4de2-b8f9-d8a98dc77548',	53051501	,'1e3573a7-3fda-4dbe-8859-7e23b278fcc4','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a56d8ada-ab03-4faa-9709-04cb01ab4398',	53051502	,'1e3573a7-3fda-4dbe-8859-7e23b278fcc4','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8c444ea3-d6f6-4a4d-8856-d92b2479bd75',	53051503	,'1e3573a7-3fda-4dbe-8859-7e23b278fcc4','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e04b9286-a62a-4a9c-9ded-cd50e799b139',	23352501	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('500d3905-093d-49a7-842a-f4ea5534b987',	23353001	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('80d0c2b4-5567-43ec-b375-c9318bc7e522',	23353002	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('200bbf78-1d4d-4ff9-b07c-aa0dce55c34f',	23353003	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c2836381-ded7-456a-b2ed-4ad3cf883b41',	23353005	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('01c38159-a3e1-479d-80f9-b43c21872414',	23353006	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b7d74ab7-ad4f-49f8-8088-c076ab578dab',	23354001	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1c3d3693-a532-4c4b-9504-681508dcff42',	23354501	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2126979d-a297-403e-922b-440413f67cf6',	23355001	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4f633257-b80a-40b1-af6f-a1a0d9ad0305',	23355003	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('41782c51-09c6-4894-acf7-899fbcff1fe7',	23355004	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2449c910-9e4b-4828-9ee1-9fe0c26476d1',	23355005	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8da07520-0411-4ed6-b107-259fe19c5e2b',	23355501	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('35ed3057-c128-4018-a247-be5b9dbd027a',	23356001	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('145f38a8-0fc2-4194-9732-6e912537886d',	23356501	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('31196765-a447-481b-852e-590412c053ed',	23356701	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('02383a27-77cd-42ac-9b6b-1373e9595877',	23372501	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e2ab44bc-9332-4703-bdee-d1d01feff153',	23374001	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d22905ef-4b9f-4d67-aac6-62a88af6ce84',	23374501	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('137a164e-8f57-4228-a37e-8b3681699ec7',	23375001	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('78c80fdf-c957-4511-b4dd-b6d3ddfc7aeb',	23375002	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7d824a4a-3d9d-4b1e-a5d9-9d7171089fc2',	23375005	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5f0a0a67-6737-4b14-8c97-cf39440dc499',	23379501	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('31c97d65-43e7-461e-acf4-6b95e81eaaca',	23379502	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bda785be-3e5c-4033-be3f-a149745314ef',	23450101	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5fa8d9bc-1e92-4ce3-9c34-369101cec400',	23450102	,'100b7713-4a7b-49b7-8cb0-c98a9cea914d','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e393a6e0-ddab-46cc-9fb1-8c360d8be8f5',	13050501	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('07581eab-646c-45a7-9f76-c2949dcc08c7',	13050502	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c789b613-99cd-4d88-8a42-fdf88c48afbb',	13251001	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8cc946af-6ccd-4307-9fec-0bb27e795d6c',	13351502	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('20aaf9ca-79ee-46ce-9420-a7c7e969d70c',	13353501	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3df442d3-82d1-4ceb-8fef-d450f3103a60',	13353502	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ced9dcd1-06a5-41db-a3fa-2db32b46268c',	13659502	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('043cf4f7-0470-4eba-9077-802e7352f05e',	13659504	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7598e68e-a8c5-4522-970b-e92970feb826',	13809504	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f4128a0a-a19c-4fff-92a2-479c7efe9409',	13809505	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3ddb493c-91ac-48c4-afa6-589135bb0696',	13809506	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9614fcf9-4032-48d1-a1fc-2f12ec3112fb',	13809599	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0b43ff37-0d0e-4553-b3c1-18b93132a806',	17052001	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a31d5301-aede-4194-b3f8-c6748c209032',	17104402	,'6bfc30d1-7122-4d7f-9e50-8fb9b87dd687','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('491c2b10-e7fd-4ed2-802f-0f9d4264f464',	31050501	,'631e20cd-35b3-45e7-8349-885bcd175a7b','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('99daf12a-a931-44e8-a03b-fd685988f6ae',	31051001	,'631e20cd-35b3-45e7-8349-885bcd175a7b','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('99899cdd-9d75-4d86-9c39-e8367868ea6f',	31051501	,'631e20cd-35b3-45e7-8349-885bcd175a7b','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('af0a6f56-44b8-41c4-a16d-4ef8284694d5',	32050501	,'631e20cd-35b3-45e7-8349-885bcd175a7b','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d2c75878-e732-4fa2-a701-f1bd259d5392',	11050501	,'d1993564-cf6d-43d8-84a2-9adaf68105e5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('454b163d-e56d-4275-b159-108b50828ee6',	11050502	,'d1993564-cf6d-43d8-84a2-9adaf68105e5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('deb6a0cb-5a6e-47a3-b6aa-c12e0c2a813b',	11050503	,'d1993564-cf6d-43d8-84a2-9adaf68105e5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e0fcd327-8d72-4c47-8741-5190a2174f96',	11051001	,'d1993564-cf6d-43d8-84a2-9adaf68105e5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('419ebaf1-e22e-4b15-99cc-b156e9ac1abb',	11051002	,'d1993564-cf6d-43d8-84a2-9adaf68105e5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('71fd2a26-7a80-4141-b1fb-174aa01740ca',	11200501	,'d1993564-cf6d-43d8-84a2-9adaf68105e5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('16bd73a7-26fa-4eac-935d-9f8141df64d0',	11200502	,'d1993564-cf6d-43d8-84a2-9adaf68105e5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f38c72d3-20c9-4938-b4ed-0f25588bb86c',	11200503	,'d1993564-cf6d-43d8-84a2-9adaf68105e5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ec911aa4-46a5-4c97-8c00-5cf6dc0a1a12',	12250501	,'d1993564-cf6d-43d8-84a2-9adaf68105e5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7e16dacb-afcf-4c56-8d2c-f437200ec642',	12250502	,'d1993564-cf6d-43d8-84a2-9adaf68105e5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3c974c80-60cd-4e0b-b0c1-3ab64b01a87e',	12450501	,'d1993564-cf6d-43d8-84a2-9adaf68105e5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4f34c3eb-d0b0-4ea4-a03f-9f0245696ab1',	61401501	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('54a0ed46-c040-4820-8e66-b07e1a2302c0',	61401502	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('496b542e-a000-416b-935a-b0435e19b0d6',	61401504	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4c8ba130-c3a9-4c87-b033-32364c4d8b4a',	61401505	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('feff7373-ef8b-49b6-83d7-184191f38770',	61401506	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e8e9e7dd-c059-4cf3-9e1c-1f6739ff7087',	61401507	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b500d287-74bd-41e1-bac1-53af24c908d4',	61401508	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1470296e-9c78-4f41-82cc-e48bf23a0e85',	61401510	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ad913362-aa6e-452e-b847-17a5f8136773',	61401512	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4e17f910-7580-43be-ae53-09f8ef371e67',	61401513	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8532798b-1754-4e77-920e-0710a80fb3bd',	61401515	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0f74b63a-0615-44db-8637-611f87f77ff1',	61409501	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3e708fe4-8d8d-46df-a713-ade58df53f72',	73359502	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8ce13832-485f-40e9-a178-6c68aeef8771',	73954001	,'831c1ed7-585a-4437-849d-afbb755820ac','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ad74d2b4-7168-466f-9130-ef89ec30d8fd',	51101001	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d34add83-1baa-434a-bd56-690402ccdf36',	51102501	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4553c878-676e-4a75-98ee-814bf3872cae',	51103001	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c41ee537-afc1-476b-b9b3-ffcfa3937dfd',	51103501	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c89cf647-a644-4435-9b76-eda3a723f277',	51109501	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('921fe1db-0d34-4732-8234-3b75cfbb5022',	51109502	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7331e0cc-fbc3-419b-9ec2-748daa322976',	51351502	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('812baf80-dfb1-4afc-9c2e-099cf4353d32',	51352001	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5e02f85b-0907-4721-b5f9-d18ebad480ba',	52103501	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('77d8804a-5ac3-4bef-bab3-4c4046b9bb08',	52351502	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b447c8eb-c52c-4826-bf2a-90d21d3d2ec2',	73109501	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8f68c5d9-30c8-47e9-b3bb-7585cc382a83',	73109502	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('12c9a00f-cb6c-4748-aaaa-704cfd339c55',	73351502	,'ca6ece8d-01e0-44a5-9184-c658693ede34','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('480e3cec-8830-4106-b3d4-78cbaef29e91',	51601001	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('87d40403-6f59-43ad-946d-c4468e28ebac',	51601501	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('001bf690-abd5-464c-94da-8e8913df5df1',	51602001	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4b289329-e947-4470-821c-45d84b4ce9d7',	52601501	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('443acbaf-3867-4c5a-bf05-d2c0dd732f16',	52601502	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e31cdcc3-23e0-4339-9fd7-13636fb8b7ab',	52602001	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('58112d4b-301c-45e0-a489-99af73c6c8a6',	52603001	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8150a2e9-e9d6-4f98-a1a6-02d56dd3cbc8',	52609801	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('dc414345-47fc-4de7-9bfb-6e8ffd5fa2e9',	53101506	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('cfe46caa-9549-4114-a1c6-0bac88c3e802',	73601001	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ef7c8037-8551-4ae2-b073-ecf9e64c8781',	73601505	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c3fa6d4b-11fa-49b8-9bda-c88f1bf18ba3',	73602005	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c055a4c3-db93-4169-9909-6be95563356d',	73602010	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1b79ba62-5662-4dd6-8725-c4f8b171db33',	73603001	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('be7a62b8-315e-4c42-9661-ea2a6fad6693',	73609801	,'4776474d-cb1e-4b05-b86e-388f8a421acd','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('fe403856-26aa-4658-ba74-9e924d647934',	41750101	,'865a24b5-0980-4f4f-962c-ea487dfe685f','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('fa9299e9-6578-4c20-bb40-dea4a23ae47a',	41750104	,'865a24b5-0980-4f4f-962c-ea487dfe685f','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('126b18e3-964f-45db-b622-3725bd7e3861',	41850501	,'865a24b5-0980-4f4f-962c-ea487dfe685f','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('610402ea-e998-4def-a33d-fce45cd00ae8',	41850503	,'865a24b5-0980-4f4f-962c-ea487dfe685f','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bbbecfa9-9a55-49f0-8b41-b41b52391bbb',	41850504	,'865a24b5-0980-4f4f-962c-ea487dfe685f','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('78ea4c91-6b38-49e9-8401-617e763f00a1',	41850505	,'865a24b5-0980-4f4f-962c-ea487dfe685f','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('aed0a69f-e631-47e2-8eff-6a758a94a77e',	42104001	,'865a24b5-0980-4f4f-962c-ea487dfe685f','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ea8912b1-fd67-4a7b-b16c-608532b08fdb',	42503001	,'865a24b5-0980-4f4f-962c-ea487dfe685f','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7d426118-1abe-4ecf-aa15-1c1bb353e9e4',	23352001	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1a5a3a68-56ed-4edc-9c1f-36f0c76db776',	23359502	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ed797ed9-2bce-4248-a222-6bc17ef968fb',	23700501	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('047a8727-b892-4577-877f-c4ec8fadce64',	23700601	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c092e46c-3e5c-4b30-8e6f-6fb9a147543e',	23701001	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('65e872bd-418b-41c6-a9b6-89880a1140b3',	23803001	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d76ef79c-601b-42b8-8d76-abb3e548c74c',	23803002	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('44ac1f98-b3f7-40f5-9e76-daeefe1bd309',	23803003	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8cd98c10-89f4-4691-a1a0-fec19ce85cd9',	23803004	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b4c74da4-0e04-4e31-8eb1-c11f5c910f85',	25050501	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7b9f04b5-ecd9-49da-9446-b3294eb63bd9',	25101001	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f7504c35-9983-4001-815a-35ce05e2e8bf',	25150501	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b9d2f689-178a-47a0-a675-0f7ce2c0b392',	25200501	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2b910135-296a-4d79-9c83-0e03a4601802',	25250501	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3c06c17b-9de9-41f5-b9f3-5765ab7bd7ed',	27059501	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2401aa37-a15f-4bbb-be63-f5c06fa1c1bb',	28050501	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a777d8df-0938-47e2-8555-79ce1ba36ee5',	28050503	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0eb0ccfe-4cff-4995-bd9d-b6ab402c685e',	28150501	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1a91f43b-9474-412e-8746-5478a516f6ec',	28150502	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a5685a74-c0a1-42e9-be4b-273aac32f5de',	28150503	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('dfe5ebc4-f006-4dd4-bcbe-d7f9824b6279',	28150504	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('837eafc4-ebec-4944-9c4f-27406fa508d2',	28150505	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('79798867-8aa2-4d18-801e-ed7b820c868e',	28150506	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('542b1d89-d617-4feb-99bf-b5b918d55a68',	28150507	,'86ad7335-84eb-402f-a1a1-4384298442da','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bf3f6d49-14cf-4069-969b-094af2555981',	13659501	,'dd2b8a3d-582a-45bd-b478-3760fd311192','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c174d08c-c19d-4e84-9e51-f329cc77a763',	17301501	,'dd2b8a3d-582a-45bd-b478-3760fd311192','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('78a88e3f-b8b1-45b3-b1d1-1637c29bc88b',	51055101	,'4244ed4b-80dc-4005-84a2-1180ef8c7cba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('55847038-8be7-4f77-87e6-a952cf2107f3',	52055101	,'4244ed4b-80dc-4005-84a2-1180ef8c7cba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f3eedca5-17a3-46f8-8dcb-bdc5c852111c',	52959522	,'4244ed4b-80dc-4005-84a2-1180ef8c7cba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('836c2d69-b2f0-4bca-bf22-d9554b12f230',	73359501	,'4244ed4b-80dc-4005-84a2-1180ef8c7cba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('57744f2a-0a94-4de6-b8aa-dce81c3a5b29',	73959508	,'4244ed4b-80dc-4005-84a2-1180ef8c7cba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('43ecf269-5638-4722-8dbd-c5bae9297c61',	52203501	,'f9f689ec-3507-4546-ac51-39eac93b8337','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ee9b26e0-d521-46b2-b91f-5b8fa91c6d08',	52359510	,'f9f689ec-3507-4546-ac51-39eac93b8337','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1365d05a-5351-4720-8a73-76aa5ff6e7e6',	73203501	,'f9f689ec-3507-4546-ac51-39eac93b8337','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8159573b-e964-4c32-8fc9-554662619118',	53052001	,'9fef8445-9e97-4eec-85ec-0d2f5929df2b','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e826dec9-ad14-4238-b7c5-44042c3c3067',	53052002	,'9fef8445-9e97-4eec-85ec-0d2f5929df2b','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7c95727b-9b72-4574-990b-5946caf45ada',	53052003	,'9fef8445-9e97-4eec-85ec-0d2f5929df2b','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('de3d3603-4732-4fd3-88e3-f0c70885a977',	16350501	,'8ea420ff-ce7d-49b3-87a2-141b37bd5f45','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ff5b943c-6a3d-4291-b666-47e4c22ab620',	16350503	,'8ea420ff-ce7d-49b3-87a2-141b37bd5f45','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e06d9c46-4f63-4568-a795-4c0bac68ee1c',	17104001	,'8ea420ff-ce7d-49b3-87a2-141b37bd5f45','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2c075000-2ab6-4b4c-9130-76da049641b6',	14050101	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7bfd90c3-bc20-4321-b069-f24a3f06b550',	14050102	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('dbdb39cf-38a8-418e-9cf2-c5159ed85cd1',	14050201	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2b386e52-0399-4c66-88f7-d31ca7205f5b',	14050202	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7d94b78d-f666-4200-9ef2-adbed58f0b02',	14300501	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ee0d0844-0348-4f14-9b79-dbf820eab9be',	14300502	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f22c7f6d-2bc1-4719-916b-5abb1b9ee132',	14350101	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1af4fb53-5696-4fcb-ae52-3460900d514e',	14350102	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('eb5f9831-1bd3-4ce7-9d87-c72fb8d2c6f6',	14350103	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c7144238-2221-4d50-88df-c8626f40e05f',	14350104	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('04e8e0ff-bc7d-4b37-941b-b326f987c3bb',	14350105	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('dbe8a8cb-f49d-4ccb-a438-82c74ed578df',	14350106	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9870d397-39ec-4437-9afe-ef25fbfef07a',	14350107	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4f9ee1f8-ad55-41cc-ad83-14674cdf025a',	14350108	,'a9cae1a9-2f04-465c-8e99-7d875ccaf392','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a8df543f-16ce-44de-bff8-dad7025d79cd',	51351501	,'09b2be89-54a9-4885-919c-6581823e3277','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('34f26a91-6136-4055-99e1-1aef5922a7dd',	51359508	,'09b2be89-54a9-4885-919c-6581823e3277','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('35bbd3d0-c2d6-49a3-b04c-d020c104d667',	51359514	,'09b2be89-54a9-4885-919c-6581823e3277','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ae63852b-eee5-482b-9a10-89b90de9f357',	51651501	,'09b2be89-54a9-4885-919c-6581823e3277','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('70e91e79-e163-4453-926b-895c4f757dde',	52250502	,'09b2be89-54a9-4885-919c-6581823e3277','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c7417d28-077f-4880-af95-fb37b8578acc',	52359513	,'09b2be89-54a9-4885-919c-6581823e3277','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b91c858f-5534-473b-9b01-7aff345cc275',	52359516	,'09b2be89-54a9-4885-919c-6581823e3277','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('83067dca-c83e-4f7a-a505-a9f497fd2e13',	73359510	,'09b2be89-54a9-4885-919c-6581823e3277','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1ac30c18-87d0-40f6-9999-21ebd414d5ec',	73359515	,'09b2be89-54a9-4885-919c-6581823e3277','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e4dd4991-1513-45bf-87dc-f5a18e5b1d13',	73359516	,'09b2be89-54a9-4885-919c-6581823e3277','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('919ac632-0fdf-4091-967d-52c5db76f469',	73651504	,'09b2be89-54a9-4885-919c-6581823e3277','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('09e09d09-7f03-41ed-a608-2bb339ff40aa',	21051001	,'455e536c-0ae1-4b46-b9e4-ca8a76f3ba42','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('aef48a70-9074-487c-82ef-f8956c555885',	21051002	,'455e536c-0ae1-4b46-b9e4-ca8a76f3ba42','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7ca5f366-180a-4628-bbf4-fcc62dc96794',	42452401	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('da4e28cf-27b6-44f5-b588-ba21a7468eb2',	51359510	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b3040ab9-b811-4dc3-b70e-2fc756af6688',	51452501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('73aee0f6-a3f6-4541-8179-8ed170b0ceef',	51452502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d0ca70f2-fbe9-4244-aaac-bbd30bea2625',	51459501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2e3c7dbc-0bda-450e-b6f7-249d2155bcf5',	51500502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8ddd40b0-d9c1-40d6-a149-3b001dc99b02',	51501002	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('403e2dd0-1dcf-439c-8ae1-0ee5ff8471c4',	51501501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3c81f2bc-cb9f-4921-86d5-225c52022c49',	51501502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2a37eea7-a642-45a4-9486-bc6ea86788dd',	51509501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5dc37cf3-4d2f-4aef-9220-f83297308d2b',	51959524	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('04052c01-7d49-4851-83a5-1c3bad4f70db',	52451001	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('280dc47b-272a-4453-8b5f-eaaaf46f5a4a',	52451002	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('83de3354-48c4-4dad-8b5d-e9ce7167eb21',	52451501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0f26388e-39ed-42b5-976d-f5fc6c6bd4db',	52451502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7a17d3df-b8b6-47ec-b111-3b83e159d03a',	52452001	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ae9cef50-ef1a-45fa-a75b-ca054b7daf0a',	52452002	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c32f8b6b-b4ec-407e-8120-27a1096ea361',	52452501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f8844cc8-45df-4781-8f9f-8b250f9ffdf2',	52452502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('24cc357f-2f73-4b9a-9c32-6847400cf11a',	52453502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('babd167d-a6b0-4c13-b9d8-762fda054639',	52453503	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b519861b-134d-42ff-b3d1-973217ea44c3',	52453504	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('dad98975-9e7e-4183-b9ca-ffc71b5d871b',	52453505	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7d1892de-5e9b-464f-860b-b830cf250cf3',	52459501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8758ed10-7019-401e-96ac-b7e94500c56d',	52459502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e36615e9-43ac-454a-83d7-dae10b5a6642',	52500501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('46a2026e-bb74-4dc3-a1c9-76594ceb30df',	52500502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a8e2dcae-005d-4599-94d5-e627d32f593c',	52501001	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a01c1c9d-28ed-4019-8d27-0406f5fca751',	52501002	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('24c4c66b-f9b5-4e7d-be84-8cb0ece831d8',	52501501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('78669155-72a5-4148-aa6a-ee5f3ca43888',	52501502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d315c8b8-8d38-42f8-b26e-18935908c7d3',	52509501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('48f57181-e196-4a3f-b85d-71631905b612',	52959524	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e7ce9666-8b04-492e-8685-8248690fb663',	73450502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4358cd42-7a90-4f47-9e5e-6daa4d0f59b8',	73451001	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('26547eaf-4b47-4318-8b8f-ca304c3720a2',	73451501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f3fb12cb-1123-46c9-90a3-4a5ad5fe8680',	73451502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('68c690fb-c01c-4c22-9311-4b543336c50d',	73452501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('28096b16-8d3e-4c34-82d2-c00ea40b2684',	73452502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d5f8deb2-58ed-49b5-a619-0d03625be0a8',	73453503	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1278f7f4-ebf2-4b7f-b387-5b71ec40761f',	73453504	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('be923d51-0657-40f4-b36a-fa000f926330',	73459501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('dee04eed-9a86-4cdd-85b0-a81542c42ae8',	73459521	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f72f6d1a-ef75-4dd1-a210-f4b94051ee75',	73500501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a483d778-c26e-42cc-8e5a-3cc7a626b7a7',	73500502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('753ee037-c049-45e6-bf83-76d4ca46b1b4',	73501501	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7d3911e5-2357-4c09-8887-57475af45610',	73501502	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0d10a252-0dcb-4c76-ad8b-edcc701e8508',	73959524	,'4d51bcb1-5537-447f-b5f1-85d6bcb44d65','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0672b0a8-f08f-4124-b604-409e01ab3958',	51054508	,'400e815d-5d6f-4463-81a4-673518dd4ad0','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4bcab86a-ee07-4792-bdcb-ee637bc03b8e',	51356001	,'400e815d-5d6f-4463-81a4-673518dd4ad0','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('13afdf0e-d235-4d1c-a860-8aa8a1362489',	51356002	,'400e815d-5d6f-4463-81a4-673518dd4ad0','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c758ffe4-133a-4c50-bec6-0b3070dd0e54',	51359504	,'400e815d-5d6f-4463-81a4-673518dd4ad0','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9c35b45a-544f-444b-8920-4020a15e931c',	51959516	,'400e815d-5d6f-4463-81a4-673518dd4ad0','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6ed7af79-474c-4909-b5aa-d0b233c4a761',	52356001	,'400e815d-5d6f-4463-81a4-673518dd4ad0','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('43e2e58d-c501-4dcf-b363-9085551483f0',	52356003	,'400e815d-5d6f-4463-81a4-673518dd4ad0','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0bd1b821-0343-44f7-8ed5-660866b8b801',	52359503	,'400e815d-5d6f-4463-81a4-673518dd4ad0','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7051619c-2919-4fa1-bd4a-90c33941babf',	73359506	,'400e815d-5d6f-4463-81a4-673518dd4ad0','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4c88615c-aa40-49be-ac1e-12d41bbd93c3',	41350502	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('06d59b70-a2cb-4ac6-b520-c9a0fd8460ac',	41350503	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('852e194f-6231-41ec-8775-28da3f893471',	41350504	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('44218006-7ef2-467b-8e00-6ef93e3e2110',	41409501	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6a71b0e5-f54c-45d1-addd-2d63b642e9c2',	42050501	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('cd91d438-17e3-42d5-b8e6-cd295a53fe88',	42100501	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('16c8bdf9-92a7-43c4-a6ea-69080b549832',	42100502	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bf1056a4-2070-42ff-a024-426cf5d2d671',	42102001	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('52911a50-2076-410b-9603-7c3a3a993582',	42505001	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3f89c613-82e4-4f7b-9cff-02d5a93d0117',	42550501	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bd5f952e-fb6f-4c48-8bc4-7d6a88ce4d2a',	42651001	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('be91e4e9-67a2-438c-9fdf-8bedd1e4f5f3',	42950502	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9959c3b8-f95a-4f58-930e-0c965db1c747',	42950601	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('cc180c78-2a83-48c0-9894-0bb5be748e5d',	42950602	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1791cb15-c529-4d8d-8f9d-16c6866c1464',	42958101	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a4220074-79f9-4a47-bb7d-407202a70643',	53052501	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9d954049-f377-44e8-aed3-00e732ae41ac',	53052502	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5e08ea56-706b-412f-8c39-62a7cf548503',	53055001	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0c631467-f66c-45fd-9311-bb86ac42f77c',	53059501	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2e967a97-df39-40ac-ac17-d3c406fe9d7a',	53101001	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e1948b80-e1b1-4088-be43-08010bf3d7e2',	53101505	,'db31a741-4c13-4713-b9c7-0a16ce6b9bda','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8f4b94d4-b1ee-40b3-99a7-b0eac1b067a6',	41401505	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('67b0385b-42df-4b4e-8ad1-00a35c59dabb',	51150501	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('81760024-a231-498e-bb2b-420116179bcb',	51150502	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6c18405e-eccf-41fc-89a5-61f07ca38761',	51159501	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a6b85962-b54e-4b6a-b867-430ab556b77f',	51159502	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('010edb6d-ed73-4ba8-a23f-be1addd71008',	51159505	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a6c7912a-a30f-41af-8656-b4254db10071',	52150501	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('66c252fd-0a04-4f18-b588-82384f91e03f',	52150502	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4220eee3-9a7f-4044-ab35-bb0502f0389c',	52157001	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('29e59b23-60a3-4604-a206-21d6fb1cd231',	52159502	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('df26c9d9-4dfa-4da3-a4a4-2936e916548b',	72157001	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2828c67f-afb1-4556-9a1e-2e25d4ff6943',	73150501	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c2f34b7a-74d2-476a-80d5-9b3c0c15ad7f',	73150502	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ff1dbef9-d111-4d63-b446-a32c146c8b2e',	73157001	,'296e0b8d-d6e6-4f95-b49c-39c9cbc23195','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ec79a026-0315-495c-bc63-18648eb8cd62',	23359501	,'a12a4bed-7c99-4e8f-b61b-594c84ddb357','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7256fffa-92d7-4a0a-88c3-f9fdb58c326f',	17309501	,'793f826b-8847-4860-9ed0-198e50ea55b7','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8201017c-bed5-459b-8c6c-96578f8cce46',	41409502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e6b80690-5bc5-4add-82dc-d6a067506edd',	41850506	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('22832aee-39ae-4642-b846-870427856f2b',	42503501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0b5eddc4-9d68-43d6-86a1-1e7ea7901fcf',	51300501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f9e34e95-3e52-47d7-bc39-b03b99757038',	51302001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9e821017-1fe2-4a90-8639-349127ee63c4',	51303501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('90174b88-7390-4d84-aac8-b6826ac459d1',	51309502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1f9cf65a-be6d-4e6a-937e-29e123cd959b',	51350502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a61fbcc0-e381-4224-b872-2113051cea25',	51355001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3c509a1b-e66d-493d-aed3-df0eff60dc1f',	51355002	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f4e651b2-54d8-4300-b745-86f6baff81d0',	51359501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('fc079a31-756c-4631-8b2b-08997a7a7590',	51359502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('93422410-cb12-4a0e-8a34-bc56f1eb32e6',	51359505	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6599931b-90d3-4b76-a3ea-e64a656acabf',	51359511	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('904b8fe9-339c-4197-8515-2e22a0248152',	51359513	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a741540d-c59c-4102-8bf4-93706f59136f',	51400501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a10d8161-ca88-4f5f-98a1-55d0a27cded4',	51401001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b773e212-bda9-4739-9118-e79a8ab3913d',	51401501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4dabfcf1-828a-4666-a524-28d7052154d4',	51451501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8b5625f5-5417-4cc0-9fc1-15e118190232',	51550501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9d17462b-cb02-4124-9699-4748fed96266',	51551501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4dc252cf-597b-4aa9-8c2e-d799c6a31090',	51551502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4adb1101-b76d-4f35-b2b5-b29eea177808',	51559501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d9402ede-3813-4288-8661-4dbbc521f1be',	51651504	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f04dc4f8-885d-4c68-932b-606234467224',	51950501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6890d342-7728-491b-b969-760b7654a152',	51951001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8b925c2c-0381-4291-a493-7e42ce09bc44',	51952001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ead691f9-9085-481c-b931-89993ab827a5',	51952501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5a124933-23a7-446b-b8f0-d86db0dfe5f3',	51952502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('350712dc-4077-4b12-b98a-efddf88f9f63',	51953001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e200e913-5c3b-45b1-b34d-2314bcd5eb38',	51953002	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5b4c300c-4d0e-442e-b4ab-a0bbb03f2a9b',	51954001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6f977b64-55f7-4a01-bdf8-2f3ae8f89bac',	51954501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('414dfcfc-e0a0-44b4-bd98-899c0374e82a',	51956001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('00b2d771-00f9-43df-ad4c-9af1752c21d0',	51956501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e3a55433-ff46-4cdd-93f5-b1a303d29c8d',	51959502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7432693b-66d8-4a2d-b309-f9e63c39ace3',	51959505	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c233b16b-0b9b-4946-bd76-b4eebc1d05b1',	51959508	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('62969f03-5961-4ac5-9aa3-9f266c38cc9b',	51959509	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('429c4d0a-070f-4816-b85d-9802ba7c8287',	51959511	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ab5c0b6d-d721-4a9b-a011-07247d05152c',	51959513	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1bb34cd4-485e-469d-adaf-aaa29a6bed04',	51959514	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('39070e67-0588-42ee-8493-714764d284bc',	51959515	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('67d2bbd9-2c5a-4c68-a925-3a0930e12b13',	52109501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('68ab4adf-5c6f-4f96-8953-17686e1716e7',	52109502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('28f3078f-8461-45fb-8b3a-b73ea4929b2a',	52301001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0e726139-418a-4f97-ba94-9379030cbbbd',	52302001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('47ba27d4-cdbf-4ef5-b094-28e7388e83d0',	52303501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('05bd7878-3da1-4caf-ba7e-6b63750e3050',	52306001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('26220ba5-1339-43fb-812d-9a09972114cc',	52309501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7a806b9f-cd33-4a42-87a2-898c65bd6e22',	52309502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7ea53c06-3735-4556-a4c6-2f7c27e807d7',	52350502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1b3b788c-a6dd-42fd-8401-af4531e7b94f',	52354001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('31b4d813-70db-4656-9477-487107cbf94b',	52355001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9144ba10-ff83-4ec4-94fb-aeaccd65fba3',	52355002	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7803eea4-1394-4c2f-8458-5c5869268ed2',	52355003	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('10a8fbc7-a07d-4f68-aee9-a37ce24498af',	52355004	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ea4e4f0b-bf88-41a1-9144-ff77ec5d7884',	52359501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f1aabc7c-2302-40b5-8bd7-7e066db808d7',	52359502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('23c085be-aaae-4565-b68f-e5f590d19cf5',	52359504	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3ba0377f-41aa-4ea3-8bad-45930178aea2',	52359511	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('48775d85-5c2b-4051-ab6c-54d267724950',	52401501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a8c64971-1580-4cfe-8c60-d0c126fb2842',	52950501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9179d629-bab0-4a5d-ae85-c1e232bcb3e9',	52952501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5f848726-8a91-4840-a410-b2a70a9f9ed6',	52952502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d8d187f0-dc3a-4641-a8e9-0dbaaef7c548',	52953001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f6c19fed-a10a-4b8b-a629-af641221846c',	52953002	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('61638cf0-e9c2-43f5-bcae-a7478065da03',	52954501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('349f1cc1-756d-406b-88c7-0ad22a0894d7',	52956001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('20efd628-86de-4981-bebe-b937175e3eaf',	52956501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c913619a-4db5-4eea-b2c5-ba8bfaf6510a',	52959501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4348b071-af58-48ff-8d73-5447e3453014',	52959502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2fa72d4a-e977-40db-bc85-84e785200701',	52959503	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b6ea26be-7d74-43af-85bf-6f479f3bc950',	52959504	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('30ae364a-59bb-4652-b0fb-130ee314e04d',	52959505	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2e91f02c-3774-411f-b784-6aa562a8f62f',	52959508	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('82a0f99b-97d7-400e-bf34-e314c658eec0',	52959509	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ffcc9e45-7887-49d4-b9a3-77d853405cd9',	52959520	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8dcf5e7b-f60f-48ab-8b37-09dbf7aa6911',	52959521	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('44df20c7-7325-4d74-a749-b4f58f2d8934',	52959523	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7c0101fb-5ca5-4106-a4d5-f31c83c5fc9b',	52959525	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('20756cc2-4b7b-49d7-8e81-27371032f5cc',	52959529	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('815f43f9-5d72-4b59-b1db-e10ff42bc739',	53151501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6c6c25d5-8fa2-4e22-a382-42c6a0fa53a1',	53151502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c7299b7b-45be-4fd2-b94c-3689531452af',	53152001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0cdf8da0-0ab4-4bc9-91f6-53acded190a9',	53152002	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d0bc1dac-a4f3-4d6e-b54d-d49fd7d581b8',	53952001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('808a1692-fd5e-4a09-8415-e545144a4197',	53952503	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1ebaf20b-dfc3-42ae-9da5-6af7d9d5563a',	53959501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d100c157-0e0a-44cd-a490-0e3a0bb87e5d',	53959502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d3874e2a-453a-4679-a588-b18c4873b5f0',	54750501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('caf31e80-d48d-496e-b7d9-f2a7f8f39017',	73109503	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8970b084-6a1b-4459-8d29-135660ae99a9',	73302001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('dbf5b8e0-2571-4aa8-98a8-ba12c6d54222',	73303501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('501b53b5-82b9-4de4-ae50-83d4cb5aa52a',	73309502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('88a08fa9-54be-415f-847d-1b7a066631fb',	73350502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f5bcf33a-f86f-4254-bec6-4b933d3e76e3',	73354001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('558c4a66-5bfb-44f7-b1d9-90cee4413e18',	73355001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('19345d8f-ed23-437f-bfe1-56a4cbce3770',	73356001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('cfdf8577-b12f-4ce7-91e8-5a8671793fe3',	73359505	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bab49fad-6fd4-447a-88d3-f51ba7534c56',	73359508	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('cb9c93bf-0cc4-4c38-98b2-b8da377b23e5',	73359514	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3c0e5374-89cb-42b7-9b3f-af8e87eb4332',	73952501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8003c702-e87a-401a-af67-81b6b9aded44',	73952502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4c66c2e8-a1d1-4b02-9e57-ac56e440affa',	73953001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8cac5fd7-1fa7-475e-b5fa-778a0723b9f8',	73953002	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1a83dce6-483d-4e17-85cc-7de233cddeed',	73953004	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d0d89f19-e430-4d9b-b9e2-ddd46d96da5f',	73954501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('57aa418c-72c0-4c08-88bb-ac04701a4457',	73956001	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7e516146-0882-4464-8be9-1fc9925864ce',	73956002	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0e584922-e31b-49f5-ae7d-003bda4556dc',	73959501	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('364d15cd-037b-49fb-955f-32c04bbee834',	73959502	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2523dfd9-f789-4266-b9b3-e06481fc1015',	73959503	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('53d661c4-f3ad-400c-89f8-e774b9a28409',	73959504	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('67027582-ae36-4f91-aa34-e20f1d6276ba',	73959506	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2b0c2bc4-7e10-483c-94a8-eb7910103ce1',	73959507	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7dbfbcc0-0143-4120-bc52-b2a1c9e61e61',	73959509	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8403b936-244e-4ceb-a6fa-d977a983d0f6',	73959510	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7c9f4b34-eb23-4689-8823-68fb4ba71138',	73959511	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a1ea386d-f44b-4a6e-9654-38eabe903607',	73959520	,'fcc48fd7-c853-4d65-a042-806cf2c9eac5','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('83ce1928-c30c-4211-8876-267606f773dc',	28150598	,'529f69d1-4e9b-46dd-aa29-0a1c4c4d36f0','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8ce6abe3-d8d2-4df5-aee1-7d0b74819388',	51050301	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('14603149-1e10-4ce0-9b00-4fca1768a781',	51050601	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('18241978-1e8d-4d5f-9754-89d9e4745dd8',	51050602	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('179fb6f5-3984-4afb-9728-b21a6f87d80c',	51051501	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('292ade20-844c-49ce-9d7e-3d8dc90e0137',	51051502	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5d1bb752-0661-4483-bcfe-0bd9fbec34b4',	51051503	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1143b5e5-6191-4e39-afa2-553385f1aa62',	51051801	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('42d9bf95-2aea-44c9-91b8-a61380c966c8',	51052401	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('780cc548-e86f-402e-b93b-ff797fafc1f9',	51052701	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ca606aee-76dd-46c4-a81b-21b49d259b61',	51053001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5a48d1fc-f839-4ca9-ab96-15fe754ec0a1',	51053301	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('576f0ed2-d270-4b48-88b9-cb7212df6c88',	51053601	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d1c8ef9d-0b3f-4b16-a3bf-60d5231c5be0',	51053901	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('26d8b45f-9955-4c41-a4da-21be81250c1d',	51054501	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('cdf936bc-2c34-438a-8559-07da945d2f3b',	51054502	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('01a6ab9a-a00a-4a86-9983-44d8daa0ccc7',	51054503	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f022ef10-88ae-420b-b576-d04976e9b938',	51054504	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('43bd4690-bb71-4c33-8ef8-8a61c5706675',	51054505	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('eae1aabd-a0c0-49ea-ab6e-13b67a303b66',	51054507	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9053a810-e43f-4c48-b33b-0ae7b184d1c1',	51054801	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bbc16dc0-96b5-43f1-aa2a-b9927e969b75',	51056001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8ebc596d-99b4-48c2-a3eb-1fb8ac57f007',	51056301	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('645cdfe7-df29-4c04-9023-192561519bd3',	51056801	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a7e49c1e-f9da-46a7-904d-bda9246c55a7',	51056901	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7949fa68-64d4-46de-851f-a74da940fb4a',	51057001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5e6b3170-ce01-42cb-ad3e-6bc307e264e0',	51057201	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('79c9d4f8-192a-4b07-ad9a-abcbde1a7b5d',	51057501	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bb999469-611b-4194-b3e0-4f1a156238ba',	51057801	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('53a54b19-04ea-45aa-8175-9ee10fcba871',	51059502	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a8c84171-f97b-423b-b311-73a0c2cef9e0',	51351001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6739bb9b-c79f-4a36-934f-30f3a913991d',	51359503	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a2ef2b59-63ce-4f15-a1b3-e848855b3730',	51959510	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e68a6f7d-b6e7-42bf-813c-b1204766df97',	52050301	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('60f00a5b-b6a6-42aa-a2b6-ac34aab28cd5',	52050601	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d4baeb92-e5a1-4585-baba-38ed3b30028c',	52050602	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0dbc85cd-d169-4c6b-b6b0-7a3701c86c81',	52051502	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5d11bb97-2c0f-4797-9ce2-714924b63da2',	52051503	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('933b52fd-c27f-4307-9e30-ce90af2b3ecd',	52052401	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('71365a6c-dceb-495c-916e-43f4e56981c9',	52052701	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bcfdfef7-8dd7-44bb-b511-f25726437204',	52053001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('cf12bbf8-9ac9-4ea7-95a4-36b5e6f9098e',	52053301	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e55ac254-2257-4635-941e-b70b38e3f5c4',	52053601	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ee875680-af7a-487a-9255-af138642e654',	52053901	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('99077d61-02b5-4169-bbe9-48d2bfc35ad3',	52054501	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('732adfc6-8a9b-44ac-911d-22f911c267c8',	52054502	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6ac623d9-fa55-4b31-a9e3-7e0a59fefd03',	52054503	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a343df36-a961-44e8-ad72-3e07589c389e',	52054504	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b2106861-3977-4f1c-87df-9456172e1115',	52054505	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0b0c0099-4eec-47b8-93e9-d9e4729e9a5f',	52054506	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('32a317fc-4280-4bb9-8d30-e69b15257336',	52054801	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('285d9c7c-a43b-453f-8816-ba8425547289',	52056001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('06d2f3f6-894c-4936-a406-fdaaf232e87e',	52056301	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a38f6265-c973-4a4e-aba1-e7076c5186ad',	52056801	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3a60b084-de1c-4b7c-9450-9eb34c2dc948',	52056901	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1c3ac49b-92a7-4259-90b6-a03a949a084d',	52057001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3ef77b03-263d-45a3-9ae6-745565f5409c',	52057201	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('44fe6f47-b89e-4fee-9dc5-37981d869548',	52057501	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('fc133bac-c2bb-47c0-82cc-b6b20567e6bd',	52057801	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e3c45b2c-59f6-495c-903d-c4e626e1186b',	52059502	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8bce7748-2897-4cb8-a0fe-6c9c6f4392cb',	52359517	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('75823415-8753-4a15-8a13-506d63b964d5',	72050301	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1bd82bd4-5d0b-4946-ad3b-2793e5db2270',	72050601	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f9407515-e9e7-46f4-abff-12734656b9f5',	72050602	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3d3a256c-3365-43c1-bb46-29d0a3b73ab6',	72051501	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('875ee58f-03be-4d9a-9325-816c6b10004f',	72051502	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('834da529-71ed-498c-8dd8-fd979ef9ea0a',	72051503	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2dba4f92-9e7b-4d88-acb5-0d3b3ca0222a',	72052401	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1f4e6725-b725-4164-871e-a31111326c00',	72052701	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('46553af6-6e83-4f56-84ba-daf007ac36d7',	72053001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('aa3f1d58-8bef-4d33-80d7-5a3791f5457b',	72053301	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e82cffe8-080d-404b-a339-b414ebd9df62',	72053601	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('79a795fd-d0be-4df1-a3e6-6f8a9a77ff78',	72053901	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f3fced31-1532-43c0-83e0-5a9d5f4a1163',	72054201	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('de871bcc-08ee-437b-b4b2-ab85b16ce067',	72054501	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d8bc7ed9-8528-4f80-b806-d289e4e49ee6',	72054502	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('90971287-cdf4-412e-b581-d66816716d37',	72054503	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6cf4686d-6ed7-471f-8bfa-6e3a59ffe9b4',	72054504	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d949cbb4-ef42-47e4-b319-fdfc7cfc8c7e',	72054505	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('418f7940-6ff1-4c47-8c84-36909fe4d4ba',	72054506	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b301451c-d1df-42a7-9ed9-4dfe25c54185',	72054801	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3719d7c2-f754-47a1-913f-d828cdb77a7c',	72055101	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c7524c35-cbdb-4f35-a55f-79048fa12ef9',	72056001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4516867e-9b07-4821-8d6c-cc823fba1729',	72056801	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0943ee33-9f7c-489e-8a0e-276f361f33a6',	72056901	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('11b84e19-da43-432e-83e0-97cebc6a80e2',	72057001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9ebb7a72-bc32-4bc9-947f-04584aa97ca2',	72057201	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4ea392f2-06ee-435b-8afc-821d275de9ef',	72057501	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e5fa0fd0-ff5a-4211-be73-82c5928a297a',	72057801	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('602c66e2-bb4c-437f-9299-521bf6100666',	72057802	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3b6d7329-6a9a-4c46-a824-42c310017de5',	72059501	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('79733b76-d88a-4927-b5c0-e8e1cc209471',	73054501	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('818d3e00-8b5c-444a-bf02-2a9fed785a72',	73055101	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('37543b46-12fe-4236-9f62-a6e72e239b08',	73056301	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('42c3160b-3176-4fae-b8a7-d30a8e988eba',	73056801	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ae8874f3-b8e0-4727-81a2-4204209eff10',	73056901	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5fba8545-68de-4c3d-886b-57ee0dea60a3',	73057001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('196b08b8-a55b-40f1-98fe-e2bc17a8be7f',	73057201	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e4b9a037-fd35-43da-b61b-0ebf44c4e0f1',	73351001	,'d2127179-b1b0-4b02-8cdd-ec9185e6c942','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('0ce96163-faf7-4cbe-8c5b-7e99fdcb4edc',	15240501	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d5bb5e96-25c4-4576-9dcf-d963bcdcf2dc',	15280501	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ea0c4fce-f57e-498c-a7c8-0e49e7378375',	15281001	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b33e8c4c-1393-45e7-b074-7d016563b9a1',	15361001	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d34801cf-55d8-45dc-bf28-076db02c5cfa',	15361002	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ca28dc94-731e-44d6-bdb8-0fb9c6cd4ca5',	15361003	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ac75cafa-2c3c-408f-b947-c856277ac9a3',	15369501	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e6c5e5da-c0eb-4961-a4c7-913beef46de9',	15369502	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b01c8006-600a-4f4d-8cff-44ee5ec61bca',	15369503	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bf44cb24-2d73-43e0-b8e8-bc41c9c81f0c',	15369504	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2b9f9909-dd3e-4fc2-b9d5-0a2aa6f4ba6a',	15369506	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d081f74a-274d-45d3-af62-3d18d34651aa',	15670101	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5e2f3461-95a3-4989-8cea-c7ecda1ec912',	15889001	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bf9f5683-eff1-4932-bd91-aed874c994c7',	15921502	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9c729579-7db4-49a5-8a79-9f1228c11ac3',	15922005	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('16d3d7a4-88cb-4ecc-8e72-423d8a3379f8',	15923001	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a90a4d25-6e46-4a99-b0ef-09c4a2e36687',	15923003	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7142584e-95cf-435d-8e54-e2d8f9ab748f',	15926701	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e7d9692a-92e0-41c6-94b6-d2614e3fec32',	17100801	,'ef27374a-97ea-4273-83c9-5e2f53254311','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('28e376e8-8550-4789-92ee-c1427871d8a1',	23150501	,'739ed474-b50e-4ef2-b1ca-3e7ab689d992','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b042b5ca-1e8b-49fd-9df8-f0ca0b05d8d5',	51201001	,'a88558e9-d650-433b-a775-fdc328316bfe','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6612473f-d29a-46b4-a048-5004e71cea36',	51202501	,'a88558e9-d650-433b-a775-fdc328316bfe','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f0bf8279-eb2b-4020-8931-c42de595941e',	51209501	,'a88558e9-d650-433b-a775-fdc328316bfe','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('66a8b3c3-bdc7-42a6-aa31-896f32a90da7',	51359509	,'a88558e9-d650-433b-a775-fdc328316bfe','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('66cbc84a-3fd2-48cb-ba8e-64b7d170e1a9',	52201001	,'a88558e9-d650-433b-a775-fdc328316bfe','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('dcc6b26e-cb77-4348-949f-947dd2fa6174',	52201501	,'a88558e9-d650-433b-a775-fdc328316bfe','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('af6eebca-adc9-466f-b5ac-6ca7bfb543a5',	52202501	,'a88558e9-d650-433b-a775-fdc328316bfe','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('32bdd89f-0999-402e-8937-ffc9a3a9f492',	52359508	,'a88558e9-d650-433b-a775-fdc328316bfe','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('91076a5d-e151-4674-b488-fa8245b134a2',	73201001	,'a88558e9-d650-433b-a775-fdc328316bfe','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a6934a3a-843e-4141-a2af-68f34ce7473a',	73202501	,'a88558e9-d650-433b-a775-fdc328316bfe','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('48f74a1f-2410-44a2-b7e4-a674d2cb55cc',	42950501	,'4364d59b-2a49-4629-a6ff-1daffbbab541','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ccb3954c-8e1e-47dc-8054-8020f3d4a774',	21201001	,'fa576cfd-264a-4b77-8488-6dc475f521e3','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('cff34030-7b9a-4a4a-83d7-c53b98094945',	21201002	,'fa576cfd-264a-4b77-8488-6dc475f521e3','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9ca6c5bd-6948-4eba-ad89-8b97c4fd2865',	21201003	,'fa576cfd-264a-4b77-8488-6dc475f521e3','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('115f45ff-46be-4d4c-ad73-38e9666d580a',	23350501	,'fa576cfd-264a-4b77-8488-6dc475f521e3','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('89dc76b6-825c-411d-8cd0-31c606e79a52',	22050501	,'edf43379-4387-44a7-a761-7d0cb4f38b66','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b822b9c9-017a-42c1-806c-b7d2a60add56',	22050599	,'edf43379-4387-44a7-a761-7d0cb4f38b66','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('416dc7cd-5f94-496e-9fc1-055a3d914abb',	23051001	,'edf43379-4387-44a7-a761-7d0cb4f38b66','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6c780b08-8431-447f-854f-bdf4193f68a8',	17300501	,'923f343d-074a-4493-8906-334f47670669','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('50a4eaf5-9626-45cc-a7e6-5cbea6127d7a',	17300502	,'923f343d-074a-4493-8906-334f47670669','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8a07d85d-1da1-486b-9ef1-1f8c7757d92d',	17301001	,'923f343d-074a-4493-8906-334f47670669','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('db744d2c-6819-41ec-885f-059352e50fd2',	17301002	,'923f343d-074a-4493-8906-334f47670669','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('82325d00-2e78-4082-9135-58d0c9481998',	17109501	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2edf4da5-6a24-4163-ba73-f1fd0e540a53',	18551501	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9874b3b8-44dc-4108-a60c-306f859f396d',	18551502	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ed9c5077-c369-4130-a7b0-89ffb19c015c',	18551503	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7e6f39a4-0488-44dd-91ac-0145b8228570',	18551504	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('69e35716-8626-423e-a7d6-5997ee885513',	18551505	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('09da177c-c20c-4f21-93b3-225c6c5cf602',	18551599	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('167a0c15-8861-4fe2-88a9-44db19bae8b8',	18551701	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('11fad937-d9c8-481d-8143-b47436cd4aa3',	18551801	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2431d843-2a1b-4459-acd0-3839e1cd8869',	18551802	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c1d347bf-9616-4026-9899-f44a22f4259c',	18551803	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('07dbf5bb-9daf-4434-87c2-270e95c7a407',	18559501	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('cf40ffb2-c39b-4e2c-8b06-4559734a2aac',	18750101	,'a7f2320d-fe4e-4171-80fc-f5a6af5eb5ba','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('915156f4-50e7-4bb2-86da-6b4971a9428c',	24080101	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ffd1f3f6-9830-402f-871e-598cb3128743',	24080102	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d19b9b85-cfe7-487b-9160-ef7dd70b028f',	24080202	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('035ab5bf-8166-47c4-984e-7d8cbd0f16ea',	24089901	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('241631bd-4460-4403-95dc-f142dd4a5175',	24120599	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('aef7dbf4-43c4-494e-b8dd-2df964ff5715',	24129901	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('34e4dedb-5a98-4e62-ae95-cea4db53dee4',	24130501	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3475c31d-681e-45cd-86b4-0a39d40c6596',	24650501	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('665a852c-e416-4bb1-a53b-c8cbdc92281a',	24650503	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4ffed1ef-6f58-4327-a98a-93e85b64ea4d',	24651501	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9025b89c-ebb6-4070-8a18-938a93d912c3',	24651502	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6540aa99-ca7b-4aff-a877-eedde8133187',	24652001	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5d4e6bb8-b1ab-47ad-8ef3-a97069f5fdf1',	24652501	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f1066be3-a8cb-4f19-8d8a-5f8237205351',	24652502	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9b52ecf0-e840-4c52-bc5e-eb056fc4b260',	24652504	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5ac8b8bf-129a-4e54-8ebd-316ce52bb63c',	24652505	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('dc7dc7b8-e657-4efe-be4a-2e8c4e83c7ca',	24652506	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('da1ccec5-b965-43fd-b1e9-45e7d2f622f8',	24652507	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c3b85faa-5c71-4ac9-8f50-0d12d4fdde04',	24652508	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('6a53dec2-1f33-483b-8e5a-a7f31f342917',	24652509	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('fa3b68eb-2bca-4ae1-8779-520c1d702cc7',	24652510	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('c6dc4dad-33a6-4024-8c28-f0f304311723',	24653001	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('56577889-786d-4d3f-92d9-c86a14c75b23',	24653002	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('a7b72911-7390-4f20-9ebc-db184ba69eee',	24653003	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('01d76e26-b9ee-423b-88dd-418726c49d27',	24654001	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('51be440c-d51b-4e57-a62d-b40cadc6c187',	24654002	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e01446d4-3818-406b-a626-1ea2c948ee2c',	24654004	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('96625794-37a8-450a-8dab-ce040d7a0bec',	24654005	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('9390dc66-6820-49f3-82de-29917029f320',	24655003	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('00766646-fcdb-45b6-8ebd-05bb39f12779',	24659901	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('5aae6c55-da58-4b81-9c96-985356289ec1',	24659902	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d81438ba-9615-402b-a98c-050cd0bde814',	24670101	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('fa63d1f8-80e0-4b79-a7f7-8d9b67fb9451',	24670102	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ce47c61b-2ba8-4005-94e3-7981da3af506',	24670103	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b28b9fb8-8066-4fce-9375-76910fc8f633',	24679901	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4c8b9d5e-c819-4fda-a33d-6bc796548eec',	24680101	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('924949ff-1ce6-4020-8f83-08325e69c366',	24680102	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e683af8f-4674-4c9b-8744-dbc3a4e22b8c',	24680103	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f6619738-d11c-486e-9881-920016023c9f',	24680104	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7a5f94be-7aab-4c56-9157-ba635d9cd120',	24680105	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('b4694917-c821-40c7-9af9-18391c8607d8',	24680107	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1d0b9d79-8d5c-4dd0-8c32-7601f533471c',	24680109	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7757c932-6d8b-4104-83ff-a6c3ba4e3190',	24680110	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('8303eed6-9851-4f94-831f-b2d78bd5ea29',	24680114	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1aa7e1cf-123d-479b-a629-a8eb635cd5e0',	24689901	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('e18e67a3-f100-4f9e-acab-e15af9c0a97b',	24950101	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d0d9d876-026f-4a6f-bcc8-8aefc2bd1c84',	24950199	,'9da10948-1ed5-4ee6-acc1-5c958f79e6b1','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ac70cfc9-1ffc-4144-a7a1-ef306d944c67',	51250504	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('3c4ec16d-4c88-4ce1-a4bf-3e199a040fc0',	51250505	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('53e7f0cf-5496-403e-a4dc-699f8aaa9e7a',	51350501	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('123b2e50-46a9-463a-911f-ab498707a2ca',	51352501	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d621f1a3-4e6a-4f1b-9682-ae1d4e735605',	51352502	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4cfa1633-3bdf-4627-b252-16e7b1bbbeea',	51353001	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('95114047-f0df-4077-b649-2a4a92ecf9b6',	51355501	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('26bb2266-503b-4993-8081-bd60b9f2e3e3',	51359506	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f6b29676-0bcf-45fd-8f4f-77e3b70ce23e',	52250501	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('89ae476f-27e0-4f69-a56d-03ab0bd8153c',	52250504	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('f6db102c-9fc8-4b25-ac24-5cf6d0489f99',	52250505	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('89fcffaa-84c9-45e3-a28c-67728fe102b4',	52350501	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('069074cc-a96f-4343-96cd-2d72102c26e3',	52352501	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('617ddbcf-554a-4bb3-8b3f-6d233748db5c',	52352502	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('440d8c6c-6ff1-42ce-b64e-afc807f37c0c',	52353001	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('ebfc3422-338b-48eb-9a85-c7a5c5397bf9',	52353501	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('baaee49f-a250-446d-aa7d-46f5b6adc781',	52355501	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('80acde57-c66d-49c1-9395-00e548928c94',	52359505	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('4c89aae5-579b-4a3b-960d-9233ff5ce8d6',	52359514	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('09a46a93-a065-4817-b079-1e50ababe191',	73250504	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('bac26250-2ca0-4a1d-8d77-88e1b1fbf4fe',	73350501	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('d29bfc38-ec64-45ea-a149-248c058e6782',	73352501	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('7260e76b-f1d9-423c-9d98-67a330359205',	73352502	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('2bcc2746-17cf-475b-9507-3a92d1e9708f',	73353001	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('1c41511b-95a8-4fd0-b1e8-4801ae94c363',	73355501	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');
INSERT INTO categories_entity VALUES ('282d63d5-28c5-4ed7-9679-0d9cd0435f7f',	73359509	,'802b1269-2fc1-4f93-a6b4-5d22c9f6ede6','c0d0e7d9-2b92-4f85-8b4d-df477e9a9f5d');

INSERT INTO level_entity VALUES ('32bdb33a-faf7-4026-a36f-bf3675fd7b5d', 'Unclassified assets');
INSERT INTO level_entity VALUES ('638963bf-79ff-4e5b-9048-b97bca622023', 'Unclassified liabilities');
INSERT INTO level_entity VALUES ('bb0d6df8-222e-4e68-9ca4-a6c8fa71f1be', 'Unclassified equity');
INSERT INTO level_entity VALUES ('7a73062c-7755-4bbc-b3ec-77fd706c4c98', 'Unclassified revenue');
INSERT INTO level_entity VALUES ('271f5650-d2a2-48e5-af5b-cbdc69648ae3', 'Unclassified expense');
INSERT INTO level_entity VALUES ('495b8aa2-a2d9-4cf9-9228-1035ee6b29bd', 'Unclassified cost');
INSERT INTO level_entity VALUES ('24ca144a-96cc-45b6-9d78-b71d1bec64fb', 'Other unclassified income/expense');

CREATE OR REPLACE
ALGORITHM = UNDEFINED VIEW `clarifinv2`.`categories_keys_view` AS
select
    `c`.`id` AS `id`,
    `c`.`code` AS `code`,
    `c`.`id_template_master_categories` AS `id_template_master_categories`,
    `l`.`name` AS `name`,
    `t`.`id_company` as `id_company`,
    `t`.`id_business_unit` as `id_business_unit`
from
    (`categories_keys_entity` `c`
        join `keys_entity` `l` on
            (`c`.`id_key` = `l`.`id`)
        left join `template_master_categories_keys_entity` `t` on
        (`c`.`id_template_master_categories` = `t`.`id`));

CREATE VIEW categories_entity_6_digits AS
SELECT
    MIN(ce.id) AS id, -- Selecciona el primer id en caso de múltiples coincidencias
    SUBSTRING(ce.code, 1, 6) AS code_6_digits,
    MIN(ce.id_level) AS id_level,
    MIN(ce.id_template_master_categories) AS id_template_master_categories,
    MIN(le.name) as name
FROM
    categories_entity ce
        left join level_entity le on le.id = ce.id_level
GROUP BY
    SUBSTRING(code, 1, 6);


CREATE OR REPLACE VIEW template_config_model_order_view AS
WITH RECURSIVE CTE_Hierarchy AS (
    -- Caso base: selecciona el nodo raíz (o el grupo principal que especificas)
    SELECT
        id,
        name,
        id_recursive,
        id_business,
        id_template_model,
        rule_level,
        with_formula,
        formula,
        order_template,
        CAST(order_template AS CHAR) AS path_order,
        1 AS key  -- Nivel en la jerarquía
    FROM
        template_model_config_entity
    WHERE
        id_recursive IS NULL  -- Nodo raíz, donde no hay padre

    UNION ALL

    -- Parte recursiva: selecciona los hijos del nodo anterior
    SELECT
        rt.id,
        rt.name,
        rt.id_recursive,
        rt.id_business,
        rt.id_template_model,
        rt.rule_level,
        rt.with_formula,
        rt.formula,
        rt.order_template,
        CONCAT(ch.path_order, '.', CAST(rt.order_template AS CHAR)) AS path_order, -- Acumula el orden en la jerarquía
        ch.key + 1  -- Incrementa el nivel
    FROM
        template_model_config_entity rt
    INNER JOIN
        CTE_Hierarchy ch ON rt.id_recursive = ch.id  -- Relaciona el hijo con su padre
)
-- Consulta final ordenada
SELECT
    id,
    name,
    id_recursive,
    id_business,
    id_template_model,
    rule_level,
    with_formula,
    formula,
    order_template,
    key
FROM
    CTE_Hierarchy
ORDER BY
    path_order;