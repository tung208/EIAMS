package EIAMS.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @NotNull(message = "rollNumber not null value")
    String rollNumber;

    @NotNull(message = "memberCode not null value")
    String memberCode;

    String fullName;

    @NotNull(message = "cmtnd not null value")
    @NotBlank(message = "cmtnd not blank value")
    String cmtnd;
    Integer semesterId;
    String blackList;
}