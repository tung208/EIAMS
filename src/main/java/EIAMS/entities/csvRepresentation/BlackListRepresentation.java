package EIAMS.entities.csvRepresentation;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlackListRepresentation {
    private String rollNumber;
    private String blackList;
}
