package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "semester", schema = "eiams")
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Column(name = "from_date")
    private Instant fromDate;

    @Column(name = "to_date")
    private Instant toDate;

    @Column(name = "creator_id", nullable = false)
    private Integer creatorId;

}