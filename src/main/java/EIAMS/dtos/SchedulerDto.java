package EIAMS.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class SchedulerDto implements Serializable {
    Integer id;
    Integer quantitySupervisor;
    Integer semesterId;
    Integer roomId;
    Integer slotId;
    LocalDate examDate;
}