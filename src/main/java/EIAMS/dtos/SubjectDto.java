package EIAMS.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Subject}
 */
@Data
@AllArgsConstructor
@Builder
public class SubjectDto {
    Integer id;

    @NotNull (message = "Not null semesterId")
    Integer semesterId;

    @NotNull
    String subjectCode;
    String oldSubjectCode;
    String shortName;
    String subjectName;
    Integer noLab;
    Integer dontMix;
    String replacedBy;
}