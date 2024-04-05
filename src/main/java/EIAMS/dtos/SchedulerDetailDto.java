package EIAMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class SchedulerDetailDto {
    Integer id;
    Integer semesterId;
    String semesterName;
    Integer slotId;
    Integer roomId;
    String roomName;
    String examCodeId;
    String studentId;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer lecturerId;
    String lecturerEmail;
}
