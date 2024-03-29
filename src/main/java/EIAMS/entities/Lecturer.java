package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "lecturer", schema = "eiams")
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id", length = 64)
    private Integer semesterId;

    @Column(name = "code_name")
    private String codeName;

    @Column(name = "email", length = 64)
    private String email;

    @Column(name = "total_slot")
    private Integer totalSlot;

}
