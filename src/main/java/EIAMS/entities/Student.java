package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "roll_number", length = 128, unique = true)
    private String rollNumber;

    @Column(name = "member_code", length = 128, unique = true)
    private String memberCode;

    @Column(name = "full_name", length = 128)
    private String fullName;

    @Column(name = "CMTND", length = 64, unique = true)
    private String cmtnd;

}