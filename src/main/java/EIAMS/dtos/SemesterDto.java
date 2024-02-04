package EIAMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Semester}
 */
@Data
@AllArgsConstructor
@Builder
public class SemesterDto {
    Integer id;
    String name;
    Integer creatorId;
}