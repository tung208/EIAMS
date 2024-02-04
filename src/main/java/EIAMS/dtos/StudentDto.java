package EIAMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Student}
 */
@Data
@AllArgsConstructor
@Builder
public class StudentDto {
    Integer id;
    String subjectCode;
    String rollNumber;
    String memberCode;
    String fullName;
    String cmtnd;
    Integer semesterId;
    String blackList;
}