package EIAMS.services.excel;

import EIAMS.dtos.ExportSchedulerDto;
import EIAMS.entities.Room;
import EIAMS.entities.Scheduler;
import EIAMS.entities.Student;
import EIAMS.entities.StudentSubject;
import EIAMS.repositories.RoomRepository;
import EIAMS.repositories.SchedulerRepository;
import EIAMS.repositories.StudentRepository;
import EIAMS.repositories.StudentSubjectRepository;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

@Service
public class ExcelExportDSExam {

    @Autowired
    SchedulerRepository schedulerRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    StudentSubjectRepository studentSubjectRepository;

    @Autowired
    StudentRepository studentRepository;

    public final int COLUMN_INDEX_STT = 0;
    public final int COLUMN_INDEX_ROLL_NUMBER = 1;
    public final int COLUMN_INDEX_FULL_NAME = 2;
    public final int COLUMN_INDEX_CLASS = 3;
    public final int COLUMN_INDEX_CMTND = 4;
    public final int COLUMN_INDEX_MARK = 5;
    public final int COLUMN_INDEX_SIGN = 6;
    public final int COLUMN_INDEX_SUBJECT = 7;

    public final int LENGTH_FULL_NAME = 256*26;
    public final int LENGTH_SIGN = 256*13;
    public final int LENGTH_MARK = 256*8;
    public final int LENGTH_STT = 256*5;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private int rowIndex;

    private List<Scheduler> schedulers = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();

    private final ResourceLoader resourceLoader;

    public ExcelExportDSExam(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Workbook exportScheduler(HttpServletResponse response, String listSchedulerId) throws IOException {
        Hashtable<Integer,Room> mapRoom = new Hashtable<>();
        Hashtable<Integer,StudentSubject> mapStudentSubject = new Hashtable<>();
        Hashtable<String,Student> mapStudent = new Hashtable<>();

        // Prepare data
        prepareData(listSchedulerId, mapRoom, mapStudentSubject, mapStudent);

        // Write data
        Workbook workbook = writeExcel(response, mapRoom, mapStudentSubject, mapStudent);

        return workbook;
    }

    Workbook writeExcel(HttpServletResponse response,
                    Hashtable<Integer,Room> mapRoom,
                        Hashtable<Integer, StudentSubject> mapStudentSubject,
                        Hashtable<String, Student> mapStudent) throws IOException {

        // Create Workbook
        workbook = new XSSFWorkbook();


        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH'h'mm");

        for (Scheduler element : schedulers){

            String startTime = element.getStartDate().format(formatterTime);
            String endTime = element.getEndDate().format(formatterTime);

            String date = element.getStartDate().format(formatterDate);

            String nameOfSheet = mapRoom.get(element.getRoomId()).getName() + " " + startTime + "-" + endTime;

            // Create sheet
            sheet = workbook.createSheet(nameOfSheet);

            this.rowIndex = 0;

            // Write header
            writeHeaderLine(mapRoom.get(element.getRoomId()).getName(), startTime + "-" + endTime, date);

            //Find student subject id
            List<Integer> idStudentSubject = new ArrayList<>();
            String[] strArrayStudentSubject = element.getStudentId().split(",");
            for (String s : strArrayStudentSubject) {
                idStudentSubject.add(Integer.parseInt(s));
            }

            //Create arr Student Subject
            List<ExportSchedulerDto> exportSchedulerDtos = new ArrayList<>();
            for (Integer item : idStudentSubject){

                StudentSubject studentSubject = mapStudentSubject.get(item);
                Student student = mapStudent.get(studentSubject.getRollNumber());

                ExportSchedulerDto exportSchedulerDto = ExportSchedulerDto.builder()
                        .rollNumber(student.getRollNumber())
                        .fullName(student.getFullName())
                        .cmtnd(student.getCmtnd())
                        .className(studentSubject.getGroupName())
                        .subjectCode(studentSubject.getSubjectCode())
                        .build();

                exportSchedulerDtos.add(exportSchedulerDto);
            }

//             Write data
            this.rowIndex ++;
            writeDataLines(exportSchedulerDtos);

            this.rowIndex ++;
            writeFooter();
        }

        return workbook;
    }

    // Write header with format
    void writeHeaderLine(String roomName, String time, String date) throws IOException {

        headerTitle(roomName, time, date);

        //Header table
        CellStyle styleHeader = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setBold(true);
        fontHeader.setFontHeight(11);
        styleHeader.setFont(fontHeader);
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setWrapText(true);

        //Set border
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);

        //Full name style
        CellStyle styleFullName = workbook.createCellStyle();
        styleFullName.setFont(fontHeader);
        styleFullName.setAlignment(HorizontalAlignment.LEFT);
        styleFullName.setVerticalAlignment(VerticalAlignment.CENTER);
        styleFullName.setWrapText(true);

        //Set border
        styleFullName.setBorderBottom(BorderStyle.THIN);
        styleFullName.setBorderTop(BorderStyle.THIN);
        styleFullName.setBorderLeft(BorderStyle.THIN);
        styleFullName.setBorderRight(BorderStyle.THIN);

        this.rowIndex++;
        Row row = sheet.createRow(this.rowIndex);
        row.setHeightInPoints((float) (2.6 * sheet.getDefaultRowHeightInPoints()));
        createCell(row, COLUMN_INDEX_STT, "STT\n" + "No.", styleHeader, LENGTH_STT);
        createCell(row, COLUMN_INDEX_ROLL_NUMBER, "Student ID", styleHeader);
        createCell(row, COLUMN_INDEX_FULL_NAME, "Họ tên\n" + "Full name", styleFullName, LENGTH_FULL_NAME);
        createCell(row, COLUMN_INDEX_CLASS, "Lớp\n" + "Class", styleHeader);
        createCell(row, COLUMN_INDEX_CMTND, "Số CMT\n" + "CCCD", styleHeader);
        createCell(row, COLUMN_INDEX_MARK, "Điểm\n" + "Mark", styleHeader, LENGTH_MARK);
        createCell(row, COLUMN_INDEX_SIGN, "Ký tên\n" + "Sign", styleHeader, LENGTH_SIGN);
        createCell(row, COLUMN_INDEX_SUBJECT, "Môn", styleHeader);
    }

    void headerTitle(String roomName, String time, String date) throws IOException {
        //Add image to cell
        Resource resource = resourceLoader.getResource("file:src/main/resources/fpt.png");
        InputStream inputStreamImage = resource.getInputStream();
        byte[] inputImageBytes = IOUtils.toByteArray(inputStreamImage);
        int inputImagePictureID = workbook.addPicture(inputImageBytes, Workbook.PICTURE_TYPE_PNG);
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor fptAnchor = new XSSFClientAnchor();
        fptAnchor.setCol1(0); // Sets the column (0 based) of the first cell.
        fptAnchor.setCol2(3); // Sets the column (0 based) of the Second cell.
        fptAnchor.setRow1(0); // Sets the row (0 based) of the first cell.
        fptAnchor.setRow2(1); // Sets the row (0 based) of the Second cell.
        drawing.createPicture(fptAnchor, inputImagePictureID);

        // Row 1
        CellStyle styleRow1 = workbook.createCellStyle();
        XSSFFont fontRow1 = workbook.createFont();
        fontRow1.setBold(true);
        fontRow1.setFontHeight(13);
        styleRow1.setFont(fontRow1);
        styleRow1.setAlignment(HorizontalAlignment.CENTER);
        styleRow1.setVerticalAlignment(VerticalAlignment.CENTER);
        styleRow1.setWrapText(true);

        sheet.addMergedRegion(CellRangeAddress.valueOf("D1:H1"));
//        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:C1"));
        Row row = sheet.createRow(this.rowIndex);
        row.setHeightInPoints((3 * sheet.getDefaultRowHeightInPoints()));
        createCell(row, 3, "DANH SÁCH SINH VIÊN THI CUỐI KỲ\n" +
                "LIST OF STUDENT TAKING FINAL EXAM", styleRow1);

        // Row 2
        CellStyle styleRow2 = workbook.createCellStyle();
        XSSFFont fontRow2 = workbook.createFont();
        fontRow2.setBold(true);
        fontRow2.setFontHeight(11);
        styleRow2.setFont(fontRow2);
        styleRow2.setAlignment(HorizontalAlignment.CENTER);
        styleRow2.setVerticalAlignment(VerticalAlignment.CENTER);
        styleRow2.setWrapText(true);

        this.rowIndex ++;
        sheet.addMergedRegion(CellRangeAddress.valueOf("A2:C2"));
        row = sheet.createRow(this.rowIndex);
        row.setHeightInPoints((float) (1.8 * sheet.getDefaultRowHeightInPoints()));
        createCell(row, 0, "FPTU HÀ NỘI", styleRow2);

        // Row 3
        CellStyle styleRow3 = workbook.createCellStyle();
        XSSFFont fontRow3 = workbook.createFont();
        fontRow3.setBold(true);
        fontRow3.setFontHeight(14);
        styleRow3.setFont(fontRow3);
        styleRow3.setAlignment(HorizontalAlignment.CENTER);
        styleRow3.setVerticalAlignment(VerticalAlignment.CENTER);
        styleRow3.setWrapText(true);

        this.rowIndex ++;
        sheet.addMergedRegion(CellRangeAddress.valueOf("A3:H3"));
        row = sheet.createRow(this.rowIndex);
        row.setHeightInPoints((float) (2 * sheet.getDefaultRowHeightInPoints()));
        createCell(row, 0, "Phòng thi/Exam room: "+roomName, styleRow3);

        // Row 4
        CellStyle styleRow4 = workbook.createCellStyle();
        XSSFFont fontRow4 = workbook.createFont();
        fontRow4.setBold(true);
        fontRow4.setFontHeight(11);
        styleRow4.setFont(fontRow4);
        styleRow4.setAlignment(HorizontalAlignment.CENTER);
        styleRow4.setVerticalAlignment(VerticalAlignment.CENTER);
        styleRow4.setWrapText(true);

        this.rowIndex ++;
        sheet.addMergedRegion(CellRangeAddress.valueOf("A4:H4"));
        row = sheet.createRow(this.rowIndex);
        row.setHeightInPoints((float) (2 * sheet.getDefaultRowHeightInPoints()));
        createCell(row, 0, "Môn/Course:  (KRL112; KRL112; KRL112)", styleRow4);

        // Row 5
        CellStyle styleRow5 = workbook.createCellStyle();
        XSSFFont fontRow5 = workbook.createFont();
        fontRow5.setBold(true);
        fontRow5.setFontHeight(11);
        styleRow5.setFont(fontRow5);
        styleRow5.setAlignment(HorizontalAlignment.CENTER);
        styleRow5.setVerticalAlignment(VerticalAlignment.CENTER);
        styleRow5.setWrapText(false);

        this.rowIndex ++;
        sheet.addMergedRegion(CellRangeAddress.valueOf("A5:E5"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("F5:H5"));
        row = sheet.createRow(this.rowIndex);
        row.setHeightInPoints((float) (2 * sheet.getDefaultRowHeightInPoints()));
        createCell(row, 0, "Ngày thi/Exam date:  "+ date +"         Giờ thi/Exam time: " + time, styleRow5);
        createCell(row, 5, "Lần thi/Exam type: 1", styleRow5);
    }

    void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    void createCell(Row row, int columnCount, Object value, CellStyle style, int lenght) {
        sheet.setColumnWidth(columnCount, lenght);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    void writeDataLines(List<ExportSchedulerDto> exportSchedulerDtos) {
        int rowCount = this.rowIndex;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(11);
        style.setFont(font);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);


        //Full name style
        CellStyle styleFullName = workbook.createCellStyle();
        styleFullName.setFont(font);
        styleFullName.setAlignment(HorizontalAlignment.LEFT);
        styleFullName.setVerticalAlignment(VerticalAlignment.CENTER);
        styleFullName.setWrapText(true);

        //Set border
        styleFullName.setBorderBottom(BorderStyle.THIN);
        styleFullName.setBorderTop(BorderStyle.THIN);
        styleFullName.setBorderLeft(BorderStyle.THIN);
        styleFullName.setBorderRight(BorderStyle.THIN);

        int index = 0;
        for (ExportSchedulerDto item : exportSchedulerDtos) {
            Row row = sheet.createRow(rowCount++);
            row.setHeightInPoints((float) (1.8 * sheet.getDefaultRowHeightInPoints()));
            this.rowIndex ++;

            int columnCount = 0;
            index ++;
            createCell(row, columnCount++, index, style, LENGTH_STT);
            createCell(row, columnCount++, item.getRollNumber(), style);
            createCell(row, columnCount++, item.getFullName(), styleFullName, LENGTH_FULL_NAME);
            createCell(row, columnCount++, item.getClassName(), style);
            createCell(row, columnCount++, item.getCmtnd(), style);
            createCell(row, columnCount++, "", style, LENGTH_MARK);
            createCell(row, columnCount++, "", style, LENGTH_SIGN);
            createCell(row, columnCount++, item.getSubjectCode(), style);
        }
    }

    void writeFooter(){
        // Row 1
        CellStyle styleRow1 = workbook.createCellStyle();
        XSSFFont fontRow1 = workbook.createFont();
        fontRow1.setBold(false);
        fontRow1.setFontHeight(11);
        styleRow1.setFont(fontRow1);
        styleRow1.setAlignment(HorizontalAlignment.CENTER);
        styleRow1.setVerticalAlignment(VerticalAlignment.CENTER);
        styleRow1.setWrapText(true);

        this.rowIndex ++;
        int mer = this.rowIndex + 1;
        sheet.addMergedRegion(CellRangeAddress.valueOf("E" + mer+ ":H"+mer));
        Row row = sheet.createRow(this.rowIndex);
//        System.out.println("Mer 1: "+"E"+this.rowIndex+1+":H"+this.rowIndex+1);
        row.setHeightInPoints((float) (1.5 * sheet.getDefaultRowHeightInPoints()));
        createCell(row, 4, "Tổng số/ Total:  ____ / ____", styleRow1);

        // Row 2
        CellStyle styleRow2 = workbook.createCellStyle();
        XSSFFont fontRow2 = workbook.createFont();
        fontRow2.setBold(true);
        fontRow2.setFontHeight(11);
        styleRow2.setFont(fontRow2);
        styleRow2.setAlignment(HorizontalAlignment.CENTER);
        styleRow2.setVerticalAlignment(VerticalAlignment.CENTER);
        styleRow2.setWrapText(true);

        this.rowIndex ++;
        mer = this.rowIndex + 1;
        sheet.addMergedRegion(CellRangeAddress.valueOf("E" + mer + ":H" + mer));
        row = sheet.createRow(this.rowIndex);
//        System.out.println("Mer 2: "+"E"+this.rowIndex+1+":H"+this.rowIndex+1);
        row.setHeightInPoints((float) (1.5 * sheet.getDefaultRowHeightInPoints()));
        createCell(row, 4, "Giám thị coi thi/ Proctor", styleRow2);
    }

    void prepareData(String listSchedulerId, Hashtable<Integer,Room> mapRoom,
                     Hashtable<Integer, StudentSubject> mapStudentSubject,
                     Hashtable<String, Student> mapStudent
    ){
        String[] strArray = listSchedulerId.split(",");

        // Tạo một HashSet để lưu trữ các phần tử duy nhất
        HashSet<Integer> idSchedulers = new HashSet<>();
        HashSet <Integer> idRooms = new HashSet<>();
        HashSet <Integer> idStudentSubject = new HashSet<>();
        HashSet <String> rollNumber = new HashSet<>();

        for (String s : strArray) {
            idSchedulers.add(Integer.parseInt(s.trim()));
        }

        this.schedulers = schedulerRepository.findAllById(idSchedulers);
        // Find room
        for (Scheduler element : schedulers){
            idRooms.add(element.getRoomId());

            //Find student subject id
            String[] strArrayStudentSubject = element.getStudentId().split(",");
            for (String s : strArrayStudentSubject) {
                idStudentSubject.add(Integer.parseInt(s));
            }
        }

        // Maproom
        this.rooms = roomRepository.findAllById(idRooms);
        for (Room element: rooms){
            mapRoom.put(element.getId(), element);
        }

        // Student Subject
        List<StudentSubject> studentSubjects = studentSubjectRepository.findAllById(idStudentSubject);
        for (StudentSubject element : studentSubjects){
            mapStudentSubject.put(element.getId(), element);
            rollNumber.add(element.getRollNumber().toLowerCase());
        }

        // Student
        List<Student> students = studentRepository.findAllByRollNumber(rollNumber);
        for (Student element : students){
            mapStudent.put(element.getRollNumber(), element);
        }
    }
}
