package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Lecturer", schema = "schedule")
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id", length = 64)
    private Integer semesterId;

    @Column(name = "code_name")
    private String codeName;

    @Column(name = "exam_subject")
    private String examSubject;

    @Column(name = "email", length = 64)
    private String email;

    @Column(name = "total_slot")
    private Integer totalSlot;

    @Override
    public String toString(){
        return "Lecturer{" +
                ", codeName='" + codeName + '\'' +
                ", examSubject='" + examSubject + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
