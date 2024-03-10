package EIAMS.services.excel;

import EIAMS.entities.csvRepresentation.PlanExamRepresentation;
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
public class ExcelPlanExam {
    private static IsValidExcel isValidExcel;
    @Autowired
    public void setIsValidExcel(IsValidExcel isValidExcel) {
        ExcelPlanExam.isValidExcel = isValidExcel;
    }
    public static List<PlanExamRepresentation> getDataFromExcel(InputStream inputStream,String typeExam){
        List<PlanExamRepresentation> planExamRepresentations = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("Plan");
            int rowIndex =0;
            for (Row row : sheet){
                if (rowIndex ==0){
                    rowIndex++;
                    continue;
                }

                String type = isValidExcel.getValueFromCell(row.getCell(2));
                String expectedDate = isValidExcel.getValueFromCell(row.getCell(0));
                System.out.println(expectedDate + " " + isValidExcel.getValueFromCell(row.getCell(1)));
                if (type.contains(typeExam) && !expectedDate.isEmpty()){
                    PlanExamRepresentation element = new PlanExamRepresentation();
                    element.setExpectedDate(expectedDate);
                    element.setSubjectCode(isValidExcel.getValueFromCell(row.getCell(1)));
                    element.setExpectedTime(isValidExcel.getValueFromCell(row.getCell(5)));
                    element.setTypeExam(isValidExcel.getValueFromCell(row.getCell(7)));
                    planExamRepresentations.add(element);
                }
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return planExamRepresentations;
    }
}
