package EIAMS.entities.csvRepresentation;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanExamRepresentation {
    private String expectedDate;
    private String subjectCode;
    private String expectedTime;
    private String typeExam;
}
