package EIAMS.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Student}
 */
@Data
@AllArgsConstructor
@Builder
public class StudentDto implements Serializable {
    Integer id;
    String email;
    String subject;
    String studentCode;
    Integer semesterId;
}