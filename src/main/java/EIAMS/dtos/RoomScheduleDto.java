package EIAMS.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class RoomScheduleDto {
    Integer id;
    String name;
    String type;
    Integer quantityStudent;
    Integer semesterId;
    LocalDateTime date;
}
