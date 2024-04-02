package EIAMS.dtos;

import lombok.*;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@Builder
@Setter
@Getter
public class LecturerDto {

    @NonNull
    Integer semesterId;

    String codeName;

    String examSubject;

    @NonNull
    @Email(message = "Invalid Email")
    String email;

    Integer totalSlot;
}
