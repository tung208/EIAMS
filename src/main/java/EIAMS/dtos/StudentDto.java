package EIAMS.dtos;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Student}
 */
@Data
@AllArgsConstructor
@Builder
public class StudentDto {
    @NonNull
    Integer id;
    String subjectCode;
    @NonNull
    String rollNumber;
    @NonNull
    String memberCode;
    String fullName;
    @NonNull
    String cmtnd;
    Integer semesterId;
    String blackList;
}