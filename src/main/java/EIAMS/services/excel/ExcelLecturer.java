package EIAMS.services.excel;

import EIAMS.entities.csvRepresentation.ExamCodeRepresentation;
import EIAMS.entities.csvRepresentation.LecturerRepresentation;
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
public class ExcelLecturer {
    private static IsValidExcel isValidExcel;

    @Autowired
    public void setIsValidExcel(IsValidExcel isValidExcel){
        ExcelLecturer.isValidExcel = isValidExcel;
    }

    public static List<LecturerRepresentation>  getDataFromExcel(InputStream inputStream){
        List<LecturerRepresentation> lecturerRepresentations = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("Lecturer");
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
                        LecturerRepresentation element = new LecturerRepresentation();
                        String totalSlot = isValidExcel.getValueFromCell(row.getCell(1));
                        if (totalSlot == ""){
                            element.setTotalSlot(10);
                        } else {
                            element.setTotalSlot((int) Double.parseDouble(totalSlot));
                        }
                        element.setEmail(isValidExcel.getValueFromCell(row.getCell(0)));
                        element.setExamSubject(isValidExcel.getValueFromCell(row.getCell(2)));
                        lecturerRepresentations.add(element);
                    }
                }

            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return lecturerRepresentations;
    }
}
