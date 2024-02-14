package EIAMS.services;

import EIAMS.entities.Student;
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

public class ExcelUploadService {
    public static boolean isValidExcelFile(MultipartFile file){
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
    }

    public static List<Student> getCustomersDataFromExcel(InputStream inputStream){
        List<Student> students = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("customers");
            int rowIndex =0;
            for (Row row : sheet){
                if (rowIndex ==0){
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                Student student = new Student();
                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    switch (cellIndex){
//                        case 0 -> student.setCustomerId((int) cell.getNumericCellValue());
//                        case 1 -> student.setFirstName(cell.getStringCellValue());
//                        case 2 -> student.setLastName(cell.getStringCellValue());
//                        case 3 -> student.setCountry(cell.getStringCellValue());
//                        case 4 -> student.setTelephone((int) cell.getNumericCellValue());
                        default -> {
                        }
                    }
                    cellIndex++;
                }
                students.add(student);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return students;
    }
}
