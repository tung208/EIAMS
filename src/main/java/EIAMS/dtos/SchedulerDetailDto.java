package EIAMS.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class SchedulerDetailDto {
    Integer id;
    Integer semesterId;
    String semesterName;
    Integer slotId;
    Integer roomId;
    String roomName;
    String examCodeId;
    String studentId;
    String subjectCode;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer lecturerId;
    String lecturerEmail;
    String lecturerCodeName;
}
