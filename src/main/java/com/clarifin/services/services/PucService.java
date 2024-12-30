package com.clarifin.services.services;


import com.clarifin.services.adapters.out.persistence.entities.CuentaContableEntity;
import com.clarifin.services.domain.Format;
import com.clarifin.services.domain.FormatFile;
import com.clarifin.services.domain.ResultUploadProcess;
import com.clarifin.services.domain.UploadProperties;
import com.clarifin.services.domain.mappers.FileFormatMapper;
import com.clarifin.services.port.in.PucUseCase;
import com.clarifin.services.port.out.AccountingProcessPort;
import com.clarifin.services.port.out.FormatFilePort;
import com.clarifin.services.port.out.PucPort;
import com.clarifin.services.services.util.UtilUuid;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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


  @Override
  public ResultUploadProcess uploadFile(MultipartFile file, UploadProperties uploadProperties) {
    final String uuid = UtilUuid.generateUuid();

    final ResultUploadProcess result = ResultUploadProcess.builder().idProcess(uuid)
        .status("INITIAL").build();

    accountingProcessPort.getProcessByIdClientAndDateProcessAndStateAndBusiness(
            uploadProperties.getIdClient(), uploadProperties.getDateImport(), "SUCCESS",
            uploadProperties.getIdBusiness())
        .ifPresent(process -> {
          result.setStatus("ERROR");
          result.setErrorDescription("Ya existe un proceso con la misma fecha de importación");
        });

    if (result.getStatus().equals("ERROR")) {
      return result;
    }

    final List<String> error = new ArrayList<>();

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


          final Long codPuc = Long.parseLong(
              organizedRecord.get("COD_PUC").getClass() == Double.class ?
                  ((Double) organizedRecord.get("COD_PUC")).longValue() + ""
                  : organizedRecord.get("COD_PUC").toString());

        final CuentaContableEntity cuentaContableEntity = CuentaContableEntity.builder()
            .id(UtilUuid.generateUuid())
            .code(codPuc)
            .description(organizedRecord.get("DESCRIPION").toString())
            .initialBalance(
                Double.parseDouble(organizedRecord.get("SALDO_INICIAL").toString()))
            .debits(Double.parseDouble(organizedRecord.get("DEBITOS").toString()))
            .credits(Double.parseDouble(organizedRecord.get("CREDITOS").toString()))
            .finalBalance(Double.parseDouble(organizedRecord.get("SALDO_FINAL").toString()))
            .idProcess(uuid)
            .idClient(uploadProperties.getIdClient())
            .transactional(codPuc.toString().length() >= 8 ? "S" : "N")
            .build();

          cuentasContables.add(cuentaContableEntity);

          validateValues(cuentaContableEntity, error);

          organizedRecords.add(organizedRecord);
      }

      if (error.size() != 0) {
        result.setStatus("ERROR");
        result.setErrorDescription("Error validando el proceso");
        result.setErrors(error);
        return result;
      }

      System.out.printf("Organized records: %s\n", organizedRecords);

      List<CuentaContableEntity> cuentaContableEntities = pucPort.saveCuentasContables(cuentasContables);

      accountingProcessPort.saveProcess(uuid, uploadProperties);

      List<CuentaContableEntity> cuentaContableToUpdate = new ArrayList<>();

      accountingProcessPort.getTransactionalConfirmationByIdProcess(uuid)
          .forEach(transactionalConfirmationEntity -> {

            for (CuentaContableEntity cuentaContableEntity : cuentaContableEntities) {
              if (cuentaContableEntity.getId().equalsIgnoreCase(transactionalConfirmationEntity.getId())) {
                cuentaContableEntity.setTransactional(transactionalConfirmationEntity.getTransactional());
                cuentaContableToUpdate.add(cuentaContableEntity);
                cuentaContableEntities.remove(cuentaContableEntity);
                break;
              }
            }

            });


      pucPort.saveCuentasContables(cuentaContableToUpdate);

      result.setStatus("CREATED");

      final List<String> errorValidate = accountingProcessPort.validateProcess(uuid);

      error.addAll(errorValidate);

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

  private void validateValues(CuentaContableEntity cuentaContableEntity, List<String> error) {

    double resultDouble = cuentaContableEntity.getInitialBalance() + cuentaContableEntity.getDebits() - cuentaContableEntity.getCredits();

    final String result = String.format("%.2f", resultDouble);

    if(cuentaContableEntity.getFinalBalance() != Double.parseDouble(result))
    {
      error.add("Error en el registro: " + cuentaContableEntity.getCode() + " error en el saldo final: valor esperado: " + cuentaContableEntity.getFinalBalance() + " valor obtenido: " + Double.parseDouble(result));
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
