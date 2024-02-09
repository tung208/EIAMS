package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "slot", schema = "eiams")
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "start_time", length = 64)
    private String startTime;

    @Column(name = "end_time", length = 64)
    private String endTime;

}