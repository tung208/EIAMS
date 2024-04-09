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
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

    public final int COLUMN_INDEX_ROLL_NUMBER = 0;
    public final int COLUMN_INDEX_FULL_NAME      = 1;
    public final int COLUMN_INDEX_CLASS      = 2;
    public final int COLUMN_INDEX_SUBJECT   = 3;
    private static CellStyle cellStyleFormatNumber = null;

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private List<Scheduler> schedulers = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();

    public Workbook exportScheduler(HttpServletResponse response, String listSchedulerId){
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
                        Hashtable<String, Student> mapStudent){

        // Create Workbook
        workbook = new XSSFWorkbook();

        //Test export
//        sheet = workbook.createSheet("TEST");
//        writeHeaderLine(0);
//        List<ExportSchedulerDto> exportSchedulerDtos = new ArrayList<>();
//        ExportSchedulerDto exportSchedulerDto = ExportSchedulerDto.builder()
//                .rollNumber("HE153090")
//                .fullName("Le Van Cuong")
//                .className("SE1514")
//                .subjectCode("PRN203")
//                .build();
//
//        ExportSchedulerDto exportSchedulerDto1 = ExportSchedulerDto.builder()
//                .rollNumber("HE153090")
//                .fullName("Le Van Cuong 1")
//                .className("SE1514")
//                .subjectCode("PRN203")
//                .build();
//        exportSchedulerDtos.add(exportSchedulerDto);
//        exportSchedulerDtos.add(exportSchedulerDto1);
//        writeDataLines(1, exportSchedulerDtos);

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH-mm");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("HH-mm dd/MM/yyyy");

        for (Scheduler element : schedulers){

            String startTime = element.getStartDate().format(formatterTime);
            String endTime = element.getEndDate().format(formatterTime);

            String startDate = element.getStartDate().format(formatterDate);
            String endDate = element.getEndDate().format(formatterDate);

            String nameOfSheet = mapRoom.get(element.getRoomId()).getName() + " " + startTime + " " + endTime;

            System.out.println(nameOfSheet);
            // Create sheet
            sheet = workbook.createSheet(nameOfSheet);

            int rowIndex = 0;
            // Write header
            writeHeaderLine(rowIndex, mapRoom.get(element.getRoomId()).getName(), startDate, endDate);

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
                        .className(studentSubject.getGroupName())
                        .subjectCode(studentSubject.getSubjectCode())
                        .build();

                exportSchedulerDtos.add(exportSchedulerDto);
            }

//             Write data
            rowIndex = 5;
            writeDataLines(rowIndex, exportSchedulerDtos);
        }

        return workbook;
    }

    // Write header with format
    void writeHeaderLine(int rowIndex, String roomName, String startDate, String endDate) {
        // Title
        CellStyle styleTitle = workbook.createCellStyle();
        XSSFFont fontTitle = workbook.createFont();
        fontTitle.setBold(true);
        fontTitle.setFontHeight(16);
        styleTitle.setFont(fontTitle);

        //Header
        CellStyle styleHeader = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setBold(true);
        fontHeader.setFontHeight(14);
        styleHeader.setFont(fontHeader);

        //Set border
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);

        Row row = sheet.createRow(rowIndex);
        createCell(row, 1, "Scheduler", styleTitle);

        row = sheet.createRow(1);
        createCell(row, 0, roomName, styleTitle);
        createCell(row, 2, "Start date: " + startDate, styleTitle);

        row = sheet.createRow(2);
        createCell(row, 2, "End date: " + endDate, styleTitle);

        rowIndex++;
        row = sheet.createRow(4);
        createCell(row, COLUMN_INDEX_ROLL_NUMBER, "Roll Number", styleHeader);
        createCell(row, COLUMN_INDEX_FULL_NAME, "Full name", styleHeader);
        createCell(row, COLUMN_INDEX_CLASS, "Class", styleHeader);
        createCell(row, COLUMN_INDEX_SUBJECT, "Subject", styleHeader);
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

    void writeDataLines(int rowIndex,List<ExportSchedulerDto> exportSchedulerDtos
                        ) {
        int rowCount = rowIndex;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        for (ExportSchedulerDto item : exportSchedulerDtos) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, item.getRollNumber(), style);
            createCell(row, columnCount++, item.getFullName(), style);
            createCell(row, columnCount++, item.getClassName(), style);
            createCell(row, columnCount++, item.getSubjectCode(), style);

        }
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
