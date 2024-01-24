package EIAMS.mapper;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Semester;

public class SemesterMapping {
    public static Semester toEntity(SemesterDto semesterDto) {
        return Semester
                .builder()
                .id(semesterDto.getId())
                .name(semesterDto.getName())
                .build();
    }

    public static SemesterDto toDto(Semester semester) {
        return SemesterDto
                .builder()
                .id(semester.getId())
                .name(semester.getName())
                .creatorId(semester.getCreator().getId())
                .build();
    }
}
