package EIAMS.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;
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

    @NonNull
    String rollNumber;

    @NonNull
    String memberCode;

    String fullName;

    @NonNull
    @NotBlank
    String cmtnd;
    Integer semesterId;
    String blackList;
}