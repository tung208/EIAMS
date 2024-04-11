package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "test", schema = "schedule")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "member_code", length = 64)
    private String memberCode;

    @Column(name = "roll_number", length = 64)
    private String rollNumber;

    @Column(name = "full_name", length = 64)
    private String fullName;

}