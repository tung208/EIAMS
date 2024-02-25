package EIAMS.entities.csvRepresentation;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExamCodeRepresentation {
    private String subjectCode;
    private String exam;
    private String type;
    private String examCode;
}
