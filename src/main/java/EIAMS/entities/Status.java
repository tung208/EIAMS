package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="Status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id", unique = true, nullable = false)
    private Integer semesterId;

    @Column(name = "plan_exam", columnDefinition = "integer default '0'")
    private Integer plan_exam;

    @Column(name = "subject", columnDefinition = "integer default '0'")
    private Integer subject;

    @Column(name = "room", columnDefinition = "integer default '0'")
    private Integer room;

    @Column(name = "lecturer", columnDefinition = "integer default '0'")
    private Integer lecturer;

    @Column(name = "student", columnDefinition = "integer default '0'")
    private Integer student;
}
