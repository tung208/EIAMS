package EIAMS.services.excel;

import EIAMS.entities.csvRepresentation.SubjectCsvRepresentation;
import EIAMS.services.IsValidExcel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelSubject {

    private static IsValidExcel isValidExcel;
    @Autowired
    public void setIsValidExcel(IsValidExcel isValidExcel) {
        ExcelSubject.isValidExcel = isValidExcel;
    }
    public static List<SubjectCsvRepresentation> getDataFromExcel(InputStream inputStream){
        List<SubjectCsvRepresentation> subjectCsvRepresentations = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("Subject");
            int rowIndex =0;
            for (Row row : sheet){
                if (rowIndex ==0){
                    rowIndex++;
                    continue;
                }
                SubjectCsvRepresentation element = new SubjectCsvRepresentation();

                // Lấy chỉ số của ô cuối cùng trong hàng
                int lastCellIndex = row.getLastCellNum();
                // Lặp qua từng ô trong hàng
                for (int i = 0; i < lastCellIndex; i++) {
                    Cell cell = row.getCell(i);
                    String cellValue = isValidExcel.getValueFromCell(cell);

                    // Map cellValue với header ở đây
                    // Ví dụ: header là "A", "B", "C", ...
                    String header = String.valueOf((char) ('A' + i));
                    System.out.println("Header: " + header + ", Cell Value: " + cellValue);
                }

//                while (cellIterator.hasNext()){
//                    Cell cell = cellIterator.next();
//                    if (cell.getCellType() != CellType.BLANK){
//                        System.out.println(cell.getCellType() + " "+cellIndex);
//                    }
//                    switch (cellIndex){
//                        case 0 -> element.setSubjectCode(cell.getStringCellValue().toUpperCase().trim());
//                        case 1 -> element.setOldSubjectCode(cell.getStringCellValue().toUpperCase().trim());
//                        case 2 -> element.setShortName(cell.getStringCellValue());
////                        case 3 -> element.setSubjectName(cell.getStringCellValue().trim());
////                        case 8 -> element.setReplacedBy(cell.getStringCellValue().toUpperCase().trim());
//                        default -> {
//                        }
//                    }
//                    cellIndex++;
//                }
                subjectCsvRepresentations.add(element);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return subjectCsvRepresentations;
    }

}
