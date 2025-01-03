package com.clarifin.services.services;


import com.clarifin.services.adapters.out.persistence.entities.AccountingProcessEntity;
import com.clarifin.services.adapters.out.persistence.entities.CuentaContableEntity;
import com.clarifin.services.domain.BusinessUnit;
import com.clarifin.services.domain.Company;
import com.clarifin.services.domain.DeleteCommand;
import com.clarifin.services.domain.Format;
import com.clarifin.services.domain.FormatFile;
import com.clarifin.services.domain.ResultUploadProcess;
import com.clarifin.services.domain.UploadProperties;
import com.clarifin.services.domain.mappers.FileFormatMapper;
import com.clarifin.services.port.in.CompanyUseCase;
import com.clarifin.services.port.in.PucUseCase;
import com.clarifin.services.port.out.AccountingProcessPort;
import com.clarifin.services.port.out.FormatFilePort;
import com.clarifin.services.port.out.PucPort;
import com.clarifin.services.services.util.UtilUuid;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mapstruct.ap.internal.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PucService implements PucUseCase {

  @Autowired
  private FormatFilePort formatFilePort;

  @Autowired
  private PucPort pucPort;

  @Autowired
  private AccountingProcessPort accountingProcessPort;

  @Autowired
  private CompanyUseCase companyUseCase;


  @Override
  public ResultUploadProcess uploadFile(MultipartFile file, UploadProperties uploadProperties) {
    final String uuid = UtilUuid.generateUuid();

    final ResultUploadProcess result = ResultUploadProcess.builder().idProcess(uuid)
        .status("INITIAL").build();

    accountingProcessPort.saveProcess(uuid, uploadProperties, List.of(), "INITIAL");


    accountingProcessPort.getProcessByIdClientAndDateProcessAndStateAndBusiness(
            uploadProperties.getIdClient(), uploadProperties.getDateImport(), "SUCCESS",
            uploadProperties.getIdCompany())
        .ifPresent(process -> {
          result.setStatus("ERROR");
          result.setErrorDescription("Ya existe un proceso con la misma fecha de importación");
        });

    if (result.getStatus().equals("ERROR")) {
      return result;
    }

    Optional<Company> company = companyUseCase.findCompanyById(uploadProperties.getIdCompany(), uploadProperties.getIdClient());

    if (!company.isPresent()) {
      result.setStatus("ERROR");
      result.setErrorDescription("No se encontró la empresa");
      return result;
    }

    Map<String, BusinessUnit> businessUnits = new HashMap<>();

    company.get().getBusinessUnits().forEach(businessUnit -> {
      businessUnits.put(businessUnit.getExternalHostId(), businessUnit);
    });

    boolean defaultBusinessUnit = false;
    if (businessUnits.size() == 1 && businessUnits.containsKey("0000")) {
      defaultBusinessUnit = true;
    }

    final List<String> error = new ArrayList<>();

    Gson g = new Gson();

    try {
      FormatFile formatFile = FileFormatMapper.INSTANCE.entityToDomain(
          formatFilePort.findFormat(Long.parseLong(uploadProperties.getIdFormat())));

      Map<String, List<Map<String, Object>>> data = readFileWithFormat(file, uploadProperties,
          formatFile);

      List<CuentaContableEntity> cuentasContables = new ArrayList<>();

      List<Map<String, Object>> organizedRecords = new ArrayList<>();

      for (Map<String, Object> record : data.get("data")) {
        Map<String, Object> organizedRecord = new HashMap<>();

        for (Format entry : formatFile.getFormat().values()) {

          String key = entry.getName();
          String cellIndex = entry.getCell();

          Object value = record.get(cellIndex);

          organizedRecord.put(key, value);
        }

        System.out.println(organizedRecord.get("COD_PUC"));

        if(organizedRecord.get("COD_PUC") == null || "UNKNOWN".equals(organizedRecord.get("COD_PUC")))
        {
          break;
        }

        final Long codPuc;

        try {
          codPuc = Long.parseLong(
              organizedRecord.get("COD_PUC").getClass() == Double.class ?
                  ((Double) organizedRecord.get("COD_PUC")).longValue() + ""
                  : organizedRecord.get("COD_PUC").toString());
        }
        catch (Exception e)
        {
          error.add("Error en el registro cuenta PUC: " + organizedRecord.get("COD_PUC") + " error en el formato del código PUC");
          continue;
        }

        String idBusinessUnit = "" ;

        try{
          if (defaultBusinessUnit) {
            idBusinessUnit =  businessUnits.get("0000").getId();
          } else {
            final String businessUnitCode = organizedRecord.get("CODIGO_UNIDAD_NEGOCIO").toString();
            if(businessUnitCode.equals("UNKNOWN"))
            {
              System.out.println("Error en el registro cuenta PUC: " + organizedRecord.get("COD_PUC") + " error en el formato del código de unidad de negocio");
            }else{
              idBusinessUnit = businessUnits.get(organizedRecord.get("CODIGO_UNIDAD_NEGOCIO").toString()).getId();
            }
          }
        }
        catch (Exception e)
        {
          error.add("Error en el registro cuenta PUC: " + organizedRecord.get("COD_PUC") + " error en el formato del código de unidad de negocio: codigo no existe");
          continue;
        }

        final String metadata = Optional.ofNullable(organizedRecord.get("METADATA"))
            .map(Object::toString)
            .orElse("");

        if(!Strings.isEmpty(idBusinessUnit)) {

          final CuentaContableEntity cuentaContableEntity = CuentaContableEntity.builder()
              .id(UtilUuid.generateUuid())
              .code(codPuc)
              .description(organizedRecord.get("DESCRIPION").toString())
              .metadata(metadata)
              .initialBalance(
                  Double.parseDouble(organizedRecord.get("SALDO_INICIAL").toString()))
              .debits(Double.parseDouble(organizedRecord.get("DEBITOS").toString()))
              .credits(Double.parseDouble(organizedRecord.get("CREDITOS").toString()))
              .finalBalance(Double.parseDouble(organizedRecord.get("SALDO_FINAL").toString()))
              .idProcess(uuid)
              .idClient(uploadProperties.getIdClient())
              .transactional(codPuc.toString().length() >= 8 ? "S" : "N")
              .idBusinessUnit(idBusinessUnit)
              .build();

          cuentasContables.add(cuentaContableEntity);

          validateValues(cuentaContableEntity, error);

          organizedRecords.add(organizedRecord);
        }
      }

      if (error.size() != 0) {
        result.setStatus("ERROR");
        result.setErrorDescription("Error validando el proceso");
        result.setErrors(error);
        return result;
      }

      System.out.printf("Organized records: %s\n", organizedRecords);


      final List<CuentaContableEntity> cuentaContableEntities = pucPort.saveCuentasContables(cuentasContables);

      final List<String> idBusinessUnits ;

      idBusinessUnits = cuentaContableEntities.stream().map(CuentaContableEntity::getIdBusinessUnit).distinct().toList();

      accountingProcessPort.saveProcess(uuid, uploadProperties, idBusinessUnits, "POST-READ-DATA");

      List<CuentaContableEntity> cuentaContableToUpdate = new ArrayList<>();


      idBusinessUnits.forEach(idBusinessUnit -> {
            accountingProcessPort.getTransactionalConfirmationByIdProcessAndIdBusinessUnit(uuid, idBusinessUnit)
                .forEach(transactionalConfirmationEntity -> {
                  for (CuentaContableEntity cuentaContableEntity : cuentaContableEntities) {
                    if (cuentaContableEntity.getId()
                        .equalsIgnoreCase(transactionalConfirmationEntity.getId())) {
                      cuentaContableEntity.setTransactional(
                          transactionalConfirmationEntity.getTransactional());
                      cuentaContableToUpdate.add(cuentaContableEntity);
                      cuentaContableEntities.remove(cuentaContableEntity);
                      break;
                    }
                  }

                });
          });

      //TODO quitar cuentas no transaccionales.

      List<CuentaContableEntity> cuentaContableFinal = new ArrayList<>();

      cuentaContableToUpdate.forEach(cuentaContableEntity -> {
        System.out.println("Cuenta contable: " + cuentaContableEntity.getCode() + " transaccional: " + cuentaContableEntity.getTransactional());
        if (!cuentaContableEntity.getTransactional().equalsIgnoreCase("N")) {
          //cuentaContableFinal.remove(cuentaContableEntity);
          cuentaContableFinal.add(cuentaContableEntity);
        }
      });

      pucPort.saveCuentasContables(cuentaContableFinal);

      result.setRows(cuentaContableFinal.size()+"");

      result.setStatus("CREATED");

      final List<String> errorValidate = accountingProcessPort.validateProcess(uuid);

      error.addAll(errorValidate);

      final Calendar calendar = Calendar.getInstance();
      calendar.setTime(uploadProperties.getDateImport());
      calendar.add(Calendar.MONTH, -1);
      final Date dateToFind = calendar.getTime();

      final String idProcessPrevious = accountingProcessPort.getProcessByIdClientAndDateProcessAndStateAndBusiness(
              uploadProperties.getIdClient(), dateToFind, "SUCCESS",
              uploadProperties.getIdCompany())
          .map(AccountingProcessEntity::getId)
          .orElse("");

      final List<String> errorValidateBalance = accountingProcessPort.getBalanceComparison(
            idProcessPrevious, uuid);

        error.addAll(errorValidateBalance);

      if (error.size() != 0) {
        pucPort.deleteCuentasContables(uuid);
        accountingProcessPort.updateToError(uuid, error);
        result.setStatus("ERROR");
        result.setErrorDescription("Error validando el proceso");
        result.setErrors(error);
      } else {
        accountingProcessPort.updateToSuccess(uuid);
        result.setStatus("SUCCESS");
      }


      System.out.println("Error: " + error);


    } catch (Exception e) {
      result.setStatus("ERROR");
      result.setErrorDescription(e.getMessage());
      accountingProcessPort.updateToError(uuid, List.of(e.getMessage()));
      e.printStackTrace();
    }
    return result;
  }

  @Override
  public boolean deleteProcess(DeleteCommand deleteCommand) {

    final List<AccountingProcessEntity> processList = accountingProcessPort.getProcess(deleteCommand.getIdClient(), deleteCommand.getIdBusiness(), deleteCommand.getDateImport(),
        new Date());

    final List<String> processIds = new ArrayList<>();

    processList.forEach(process -> {
      processIds.add(process.getId());
    });

    try {
      processIds.forEach(p -> {
        System.out.println("entro: " + p);
        accountingProcessPort.deleteProcess(p, deleteCommand.getIdClient(), deleteCommand.getIdBusiness());
        System.out.println("salio: " + p);
      });
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private void validateValues(CuentaContableEntity cuentaContableEntity, List<String> error) {

    double resultDouble = cuentaContableEntity.getInitialBalance() + cuentaContableEntity.getDebits() - cuentaContableEntity.getCredits();

    final String result = String.format("%.2f", resultDouble);

    if(cuentaContableEntity.getFinalBalance() != Double.parseDouble(result))
    {
      error.add("Error en el registro cuenta PUC: " + cuentaContableEntity.getCode() + " error en el saldo final: valor esperado: " + cuentaContableEntity.getFinalBalance() + " valor obtenido: " + Double.parseDouble(result));
    }
  }

  private Map<String, List<Map<String, Object>>> readFileWithFormat(MultipartFile file,
      UploadProperties uploadProperties,
      FormatFile formatFile) {

    Map<String, List<Map<String, Object>>> response = new HashMap<>();
    List<Map<String, Object>> data = new ArrayList<>();

    if (file.isEmpty()) {
      //response.put("error", "No file selected");
      return response;
    }

    try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
      Sheet sheet = workbook.getSheetAt(0);

      Long startRow = formatFile.getStartRow();
      // Etiqueta para el bucle exterior
      for (Row row : sheet) {

        if (row.getRowNum() >= startRow) {
          Map<String, Object> rowData = new HashMap<>();
          boolean allCellsEmpty = true; // Bandera para verificar si todas las celdas están vacías

          for (var cell : row) {
            CellType cellType = cell.getCellType();
            switch (cellType.name()) {
              case "STRING":
                rowData.put(cell.getColumnIndex() + "", cell.getStringCellValue());
                break;
              case "NUMERIC":
                rowData.put(cell.getColumnIndex() + "", cell.getNumericCellValue());
                break;
              case "BOOLEAN":
                rowData.put(cell.getColumnIndex() + "", String.valueOf(cell.getBooleanCellValue()));
                break;
              case "FORMULA":
                rowData.put(cell.getColumnIndex() + "", cell.getCellFormula());
                break;
              default:
                rowData.put(cell.getColumnIndex() + "", "UNKNOWN");
                break;
            }
            String cellValue = cell.toString();

            if (!cellValue.trim().isEmpty()) {
              allCellsEmpty = false; // Encuentra una celda no vacía
            }
          }

          if (allCellsEmpty) {
            break; // Rompe el bucle exterior si todas las celdas están vacías
          }

          data.add(rowData);
          startRow++;
        }
      }

      response.put("data", data);
    } catch (IOException e) {
      //response.put("error", "Error reading file: " + e.getMessage());
    }

    return response;
  }
}
