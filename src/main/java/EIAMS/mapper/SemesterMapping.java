package EIAMS.mapper;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Semester;

import java.util.Date;

public class SemesterMapping {
    public static Semester toEntity(SemesterDto semesterDto){
        String from_date = semesterDto.getFrom_date();
        String to_date = semesterDto.getTo_date();

        long timestamp = Date.parse(from_date);
        Date fromDate = new Date(timestamp);

        timestamp = Date.parse(to_date);
        Date toDate = new Date(timestamp);

        return Semester
                .builder()
                .name(semesterDto.getName())
                .code(semesterDto.getCode())
                .fromDate(fromDate)
                .toDate(toDate)
                .creatorId(semesterDto.getCreatorId())
                .build();
    }
}
