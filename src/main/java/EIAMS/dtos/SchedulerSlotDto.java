package EIAMS.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class SchedulerSlotDto {
    Integer id;
    Integer semesterId;
    Integer roomId;
    String roomName;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer lecturerId;
    String lecturerName;
    String type;
}
