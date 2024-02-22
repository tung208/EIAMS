package EIAMS.services.excel;

import EIAMS.entities.csvRepresentation.CMNDCsvRepresentation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelCMND {
    public static List<CMNDCsvRepresentation> getDataFromExcel(InputStream inputStream){
        List<CMNDCsvRepresentation> cmndCsvRepresentations = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("DS CMT");
            int rowIndex =0;
            for (Row row : sheet){
                if (rowIndex ==0){
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                CMNDCsvRepresentation element = new CMNDCsvRepresentation();
                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    switch (cellIndex){
                        case 0 -> element.setRollNumber(cell.getStringCellValue().toUpperCase().trim());
                        case 5 -> element.setCmtnd(cell.getStringCellValue().toUpperCase().trim());
                        default -> {
                        }
                    }
                    cellIndex++;
                }
                cmndCsvRepresentations.add(element);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return cmndCsvRepresentations;
    }
}
