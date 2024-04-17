package EIAMS.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class ScheduleToSwapDto {
    Integer id;
    Integer semesterId;
    String semesterName;
    Integer roomId;
    String roomName;
    String subjectCode;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer lecturerId;
    String lecturerEmail;
    String lecturerCodeName;
}
