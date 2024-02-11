package EIAMS.mapper;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Semester;

public class SemesterMapping {
    public static Semester toEntity(SemesterDto semesterDto){
        return Semester
                .builder()
                .name(semesterDto.getName())
                .code(semesterDto.getCode())
                .fromeDate(semesterDto.getFrom_date())
                .toDate(semesterDto.getTo_date())
                .creatorId(semesterDto.getCreatorId())
                .build();
    }
}
