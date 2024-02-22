package EIAMS.entities.csvRepresentation;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectCsvRepresentation {
    private String subjectCode;
    private String oldSubjectCode;
    private String shortName;
    private String subjectName;
    private String replacedBy;
}
