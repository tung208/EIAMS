package EIAMS.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ExportSchedulerDto {
    String rollNumber;
    String fullName;
    String className;
    String subjectCode;
}
