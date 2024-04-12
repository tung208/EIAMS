package EIAMS.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StudentSubjectDto {
    Integer id;

    @NotNull
    String rollNumber;

    @NotNull
    String subjectCode;

    String groupName;

    @NotNull
    int blackList;

    @NotNull
    Integer semesterId;
}
