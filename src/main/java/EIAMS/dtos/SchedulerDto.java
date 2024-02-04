package EIAMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link EIAMS.entities.Scheduler}
 */
@Data
@AllArgsConstructor
@Builder
public class SchedulerDto {
    Integer id;
    Integer semesterId;
    Integer slotId;
    Integer roomId;
    String examCodeId;
    String studentId;
    LocalDate startDate;
}