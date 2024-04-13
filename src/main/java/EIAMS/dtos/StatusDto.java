package EIAMS.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusDto {

    @NotNull
    private Integer semesterId;

    private Integer plan_exam;

    private Integer subject;

    private Integer room;

    private Integer lecturer;

    private Integer student;
}
