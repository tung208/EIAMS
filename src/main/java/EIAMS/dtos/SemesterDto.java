package EIAMS.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class SemesterDto implements Serializable {
    Integer id;
    String name;
    Integer creatorId;
}