package EIAMS.services.excel;

import EIAMS.entities.csvRepresentation.BlackListRepresentation;
import EIAMS.entities.csvRepresentation.SubjectCsvRepresentation;
import EIAMS.services.IsValidExcel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelBlackList {
    private static IsValidExcel isValidExcel;
    @Autowired
    public void setIsValidExcel(IsValidExcel isValidExcel) {
        ExcelBlackList.isValidExcel = isValidExcel;
    }
    public static List<BlackListRepresentation> getDataFromExcel(InputStream inputStream){
        List<BlackListRepresentation> blackListRepresentations = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("Blacklist");
            int rowIndex =0;
            for (Row row : sheet){
                if (rowIndex ==0){
                    rowIndex++;
                    continue;
                }
                // Lấy chỉ số của ô cuối cùng trong hàng
                int lastCellIndex = row.getLastCellNum();
                // Lặp qua từng ô trong hàng
//                for (int i = 0; i < lastCellIndex; i++) {
//                    Cell cell = row.getCell(i);
////                    String cellValue = isValidExcel.getValueFromCell(cell);
//                    String cellValue = cell.getStringCellValue();
//                    // Map cellValue với header ở đây
//                    // Ví dụ: header là "A", "B", "C", ...
//                    String header = String.valueOf((char) ('A' + i));
//                    System.out.println("Header: " + header + ", Cell Value: " + cellValue);
//                }
                BlackListRepresentation element = new BlackListRepresentation();
                element.setRollNumber(isValidExcel.getValueFromCell(row.getCell(0)));
                element.setBlackList(isValidExcel.getValueFromCell(row.getCell(2)));
                blackListRepresentations.add(element);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return blackListRepresentations;
    }
}
