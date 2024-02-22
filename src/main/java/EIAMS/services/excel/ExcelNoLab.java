package EIAMS.services.excel;


import EIAMS.entities.csvRepresentation.NoLabRepresentation;
import EIAMS.entities.csvRepresentation.SubjectCsvRepresentation;
import EIAMS.services.IsValidExcel;
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
public class ExcelNoLab {
    private static IsValidExcel isValidExcel;
    @Autowired
    public void setIsValidExcel(IsValidExcel isValidExcel) {
        ExcelNoLab.isValidExcel = isValidExcel;
    }

    public static List<NoLabRepresentation> getDataFromExcel(InputStream inputStream){
        List<NoLabRepresentation> noLabRepresentations = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("NoLab");
            int rowIndex =0;
            for (Row row : sheet){
                if (rowIndex ==0){
                    rowIndex++;
                    continue;
                }
                NoLabRepresentation element = new NoLabRepresentation();

                element.setSubjectCode(isValidExcel.getValueFromCell(row.getCell(0)));
                noLabRepresentations.add(element);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return noLabRepresentations;
    }

}
