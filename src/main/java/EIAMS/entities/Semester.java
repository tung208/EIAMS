package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

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

    @Column(name = "creator_id", nullable = false)
    private Integer creatorId;

    @Column(name = "from_date", nullable = false, length = 64)
    private Date fromeDate;

    @Column(name = "to_date", nullable = false)
    private Date toDate;

}