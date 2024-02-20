package EIAMS.services.excel;

import EIAMS.entities.csvRepresentation.RoomRepresentation;
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
public class ExcelRoom {
    private static IsValidExcel isValidExcel;

    @Autowired
    public void setIsValidExcel(IsValidExcel isValidExcel){
        ExcelRoom.isValidExcel = isValidExcel;
    }

    public static List<RoomRepresentation> getDataFromExcel(InputStream inputStream){
        List<RoomRepresentation> roomList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("Room");
            int rowIndex =0;
            for (Row row : sheet){
                if (rowIndex ==0){
                    rowIndex++;
                    continue;
                }
                RoomRepresentation room = new RoomRepresentation();

//                room.setRoomId(Integer.parseInt(isValidExcel.getValueFromCell(row.getCell(0))));
                room.setRoomNo(isValidExcel.getValueFromCell(row.getCell(1)));
//                System.out.println(isValidExcel.getValueFromCell(row.getCell(2)));

                String strNumber = isValidExcel.getValueFromCell(row.getCell(2));
                if (strNumber == "") {
                    strNumber = "0";
                }
                double doubleNumber = Double.parseDouble(strNumber);
                int intNumber = (int) doubleNumber;
                room.setRoomQuantity(intNumber);
                roomList.add(room);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return roomList;
    }
}
