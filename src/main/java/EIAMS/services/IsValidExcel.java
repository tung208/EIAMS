package EIAMS.services;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
public class IsValidExcel {
    public boolean isValidExcelFile(MultipartFile file){
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
    }

    // Phương thức để lấy giá trị từ ô
    public String getValueFromCell(Cell cell) {
        if (cell != null) {
            if (cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.FORMULA) {
                return cell.getStringCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                return String.valueOf(cell.getNumericCellValue());
            } else if (cell.getCellType() == CellType.BOOLEAN) {
                return String.valueOf(cell.getBooleanCellValue());
            } else if (cell.getCellType() == CellType.BLANK) {
                return ""; // Trả về chuỗi rỗng nếu ô trống
            }
        }
        return ""; // Trả về null nếu ô là null hoặc không được định dạng
    }
}
