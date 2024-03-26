package EIAMS.mapper;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Semester;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SemesterMapping {
    public static Semester toEntity(SemesterDto semesterDto) {
        String from_date = semesterDto.getFrom_date();
        String to_date = semesterDto.getTo_date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try{
            Date fromDate = (Date) dateFormat.parse(from_date);
            Date toDate = (Date) dateFormat.parse(to_date);
            return Semester
                    .builder()
                    .name(semesterDto.getName())
                    .code(semesterDto.getCode())
                    .fromDate(fromDate)
                    .toDate(toDate)
                    .creatorId(semesterDto.getCreatorId())
                    .build();
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
