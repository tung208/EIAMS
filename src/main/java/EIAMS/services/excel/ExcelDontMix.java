package EIAMS.services.excel;

import EIAMS.entities.csvRepresentation.DontMixRepresentation;
import EIAMS.entities.csvRepresentation.NoLabRepresentation;
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
public class ExcelDontMix {
    private static IsValidExcel isValidExcel;
    @Autowired
    public void setIsValidExcel(IsValidExcel isValidExcel) {
        ExcelDontMix.isValidExcel = isValidExcel;
    }

    public static List<DontMixRepresentation> getDataFromExcel(InputStream inputStream){
        List<DontMixRepresentation> dontMixRepresentations = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("Dont mix");
            int rowIndex =0;
            for (Row row : sheet){
                if (rowIndex ==0){
                    rowIndex++;
                    continue;
                }
                DontMixRepresentation element = new DontMixRepresentation();

                element.setSubjectCode(isValidExcel.getValueFromCell(row.getCell(0)));
                dontMixRepresentations.add(element);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return dontMixRepresentations;
    }
}
