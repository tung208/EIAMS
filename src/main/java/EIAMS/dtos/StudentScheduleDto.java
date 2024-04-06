package EIAMS.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class StudentScheduleDto {
    Integer id;
    Integer semesterId;
    String rollNumber;
    String memberCode;
    String fullName;
    String subjectCode;
    String cmtnd;
    Integer blackList;


}
