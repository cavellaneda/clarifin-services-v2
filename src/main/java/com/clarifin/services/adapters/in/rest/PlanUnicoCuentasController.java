package com.clarifin.services.adapters.in.rest;

import com.clarifin.services.domain.DeleteCommand;
import com.clarifin.services.domain.ResultUploadProcess;
import com.clarifin.services.domain.UploadProperties;
import com.clarifin.services.port.in.PucUseCase;
import com.clarifin.services.services.util.UtilDate;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/entity/puc")
public class PlanUnicoCuentasController {

  @Autowired
  private PucUseCase pucUseCase;

  private static String getCellValue(Cell cell) {
    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          return cell.getDateCellValue().toString();
        } else {
          return String.valueOf(cell.getNumericCellValue());
        }
      case BOOLEAN:
        return String.valueOf(cell.getBooleanCellValue());
      case FORMULA:
        return cell.getCellFormula();
      case BLANK:
        return "";
      default:
        return "";
    }
  }

  @PostMapping("/read-format")
  public Map<Integer, String> readFormat(@RequestParam("file") MultipartFile file,
      @RequestParam("row") int row) {

    try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
      Sheet sheet = workbook.getSheetAt(0);

      final Map<Integer, String> rowData = readRow(sheet, row);
      workbook.close();

      return rowData;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Map<Integer, String> readRow(Sheet sheet, int rowNumber) {
    Map<Integer, String> rowData = new HashMap<>();
    Row row = sheet.getRow(rowNumber);

    if (row == null) {
      return rowData;
    }

    int emptyCellCount = 0;
    Iterator<Cell> cellIterator = row.cellIterator();

    while (cellIterator.hasNext()) {
      Cell cell = cellIterator.next();
      int columnIndex = cell.getColumnIndex();
      String cellValue = getCellValue(cell);

      if (cellValue.isEmpty()) {
        emptyCellCount++;
      } else {
        emptyCellCount = 0;
      }

      rowData.put(columnIndex, cellValue);

      if (emptyCellCount > 5) {
        break;
      }
    }

    return rowData;
  }

  @PostMapping("/client/{idClient}/upload")
  public ResponseEntity<ResultUploadProcess> upload(@PathVariable Long idClient, @RequestParam("file") MultipartFile file,
      @RequestParam("idFormat") String idFormat,
      @RequestParam("dateImport") String dateImport, @RequestParam("idCompany") String idCompany) {

    UploadProperties uploadProperties = UploadProperties.builder()
        .idClient(idClient)
        .idFormat(idFormat)
        .dateImport(UtilDate.convertDate(dateImport))
        .idCompany(idCompany)
        .build();
    ResultUploadProcess result = pucUseCase.uploadFile(file, uploadProperties);
    return "SUCCESS".equalsIgnoreCase(result.getStatus()) ? ResponseEntity.ok().body(result)
        : ResponseEntity.unprocessableEntity().body(result);
  }

  @DeleteMapping()
  public ResponseEntity<String> delete(
      @RequestParam("idClient") Long idClient, @RequestParam("dateImport") String dateImport, @RequestParam("idBusiness") String idBusiness) {

    DeleteCommand deleteCommand = DeleteCommand.builder()
        .idClient(idClient)
        .dateImport(UtilDate.convertDate(dateImport))
        .idBusiness(idBusiness)
        .build();
    boolean result = pucUseCase.deleteProcess(deleteCommand);
    return result ? ResponseEntity.noContent().build()
        : ResponseEntity.internalServerError().body("Error borrando la informacion.");
  }
}
