package EIAMS.services.excel;

import EIAMS.entities.csvRepresentation.DSSVCsvRepresentation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ExcelDSSV {
    public static List<DSSVCsvRepresentation> getDataFromExcel(InputStream inputStream){
        List<DSSVCsvRepresentation> dssvCsvRepresentations = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("DSSV");
            int rowIndex =0;
            for (Row row : sheet){
                if (rowIndex ==0){
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                DSSVCsvRepresentation element = new DSSVCsvRepresentation();
                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    switch (cellIndex){
                        case 0 -> element.setSubjectCode(cell.getStringCellValue().toUpperCase().trim());
                        case 1 -> element.setRollNumber(cell.getStringCellValue().toUpperCase().trim());
                        case 2 -> element.setMemberCode(cell.getStringCellValue().trim());
                        case 3 -> element.setFullName(cell.getStringCellValue().trim());
                        case 4 -> element.setGroupName(cell.getStringCellValue().trim());
                        default -> {
                        }
                    }
                    cellIndex++;
                }
                dssvCsvRepresentations.add(element);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return dssvCsvRepresentations;
    }
}
