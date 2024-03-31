package EIAMS.dtos;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Subject}
 */
@Data
@AllArgsConstructor
@Builder
public class SubjectDto {
    @NonNull
    Integer id;
    @NonNull
    Integer semesterId;
    @NonNull
    String subjectCode;
    String oldSubjectCode;
    String shortName;
    String subjectName;
    Integer noLab;
    Integer dontMix;
    String replacedBy;
}