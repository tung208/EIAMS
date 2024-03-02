package EIAMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.sql.Date;

/**
 * DTO for {@link EIAMS.entities.Semester}
 */
@Data
@AllArgsConstructor
@Builder
public class SemesterDto {
    String name;
    String code;
    String from_date;
    String to_date;
    Integer creatorId;
}