package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Semester", schema = "schedule")
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 64, unique = true)
    private String name;

    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Column(name = "from_date")
    private Date fromDate;

    @Column(name = "to_date")
    private Date toDate;

    @Column(name = "creator_id", nullable = false)
    private Integer creatorId;

    @Override
    public String toString(){
        return "Semester{" +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                '}';
    }
}