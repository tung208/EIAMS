package EIAMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Subject}
 */
@Data
@AllArgsConstructor
@Builder
public class SubjectDto {
    Integer id;
    Integer semesterId;
    String subjectCode;
    String oldSubjectCode;
    String shortName;
    String subjectName;
    Integer noLab;
    Integer dontMix;
    String replacedBy;
}