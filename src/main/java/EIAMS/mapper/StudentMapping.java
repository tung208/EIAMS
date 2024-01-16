package EIAMS.mapper;

import EIAMS.dtos.StudentDto;
import EIAMS.entities.Student;

public class StudentMapping {
    public static Student toEntity(StudentDto studentDto) {
        return Student
                .builder()
                .id(studentDto.getId())
                .email(studentDto.getEmail())
                .subject(studentDto.getSubject())
                .studentCode(studentDto.getStudentCode())
                .build();
    }

    public static StudentDto toDto(Student student) {
        return StudentDto
                .builder()
                .id(student.getId())
                .semesterId(student.getSemester().getId())
                .subject(student.getSubject())
                .email(student.getEmail())
                .studentCode(student.getStudentCode())
                .build();
    }
}
