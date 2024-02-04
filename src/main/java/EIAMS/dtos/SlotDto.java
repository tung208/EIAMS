package EIAMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Slot}
 */
@Data
@AllArgsConstructor
@Builder
public class SlotDto {
    Integer id;
    String name;
    String startTime;
    String endTime;
}