package EIAMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder
public class StudentSubjectDto {
    Integer id;

    @NonNull
    String rollNumber;

    @NonNull
    String subjectCode;

    String groupName;

    @NonNull
    int blackList;

    @NonNull
    Integer semesterId;
}
