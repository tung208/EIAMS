package EIAMS.entities.csvRepresentation;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomRepresentation {
    private int roomId;
    private String roomNo;
    private int roomQuantity;
}
