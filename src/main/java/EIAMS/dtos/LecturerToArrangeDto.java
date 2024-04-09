package EIAMS.dtos;

import EIAMS.entities.Lecturer;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class LecturerToArrangeDto {
    Lecturer lecturer;
    int countSlotArrange;
}
