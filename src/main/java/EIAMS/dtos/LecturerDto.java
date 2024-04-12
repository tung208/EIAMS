package EIAMS.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
@Setter
@Getter
public class LecturerDto {

    @NotNull(message = "SemesterId not null")
    Integer semesterId;

    String codeName;

    String examSubject;

    @NotNull(message = "Email not null")
    @Email(message = "Invalid Email")
    String email;

    Integer totalSlot;
}
