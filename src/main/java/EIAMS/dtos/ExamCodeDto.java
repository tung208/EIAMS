package EIAMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.ExamCode}
 */
@Data
@AllArgsConstructor
@Builder
public class ExamCodeDto {
    Integer id;
    String subjectId;
    String type;
    Integer semesterId;
    Integer slotId;
}