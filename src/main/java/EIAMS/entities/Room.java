package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Room", schema = "schedule")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "type", length = 32)
    private String type;

    @Column(name = "quantity_student", columnDefinition = "integer default '20'")
    private Integer quantityStudent;

    @Column(name = "semester_id")
    private Integer semesterId;

    @Override
    public String toString(){
        return "Room{" +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", quantityStudent='" + quantityStudent + '\'' +
                '}';
    }
}