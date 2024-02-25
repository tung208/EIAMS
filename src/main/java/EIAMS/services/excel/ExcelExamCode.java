package EIAMS.services.excel;

import EIAMS.entities.csvRepresentation.ExamCodeRepresentation;
import EIAMS.services.IsValidExcel;
import lombok.Getter;
import lombok.Setter;
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

@Setter
@Getter
@Service
public class ExcelExamCode {
    private static IsValidExcel isValidExcel;
    @Autowired
    public void setIsValidExcel(IsValidExcel isValidExcel){
        ExcelExamCode.isValidExcel = isValidExcel;
    }

    static String[] type = new String[]{
            "","exam",
            "IT","Reading","Listening","Opencode","PE","TE","Writing","Vocabulary",
            "Writing VN-EN","Writing VN-EN"
    };

    public static List<ExamCodeRepresentation> getDataFromExcel(InputStream inputStream){
        List<ExamCodeRepresentation> examCodeRepresentations = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("ExamCode");
            int rowIndex =0;
            for (Row row : sheet){
                if (rowIndex ==0){
                    rowIndex++;
                    continue;
                }


//                // Lấy chỉ số của ô cuối cùng trong hàng
                int lastCellIndex = row.getLastCellNum();
                // Lặp qua từng ô trong hàng
                for (int i = 2; i < lastCellIndex; i++) {
                    Cell cell = row.getCell(i);
                    String cellValue = isValidExcel.getValueFromCell(cell);

                    if (cellValue != ""){
                        ExamCodeRepresentation element = new ExamCodeRepresentation();
                        element.setSubjectCode(isValidExcel.getValueFromCell(row.getCell(0)));
                        element.setExam(isValidExcel.getValueFromCell(row.getCell(1)));
                        element.setType(type[i]);
                        element.setExamCode(cellValue);
                        examCodeRepresentations.add(element);
                    }
                }

            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return examCodeRepresentations;
    }

}
