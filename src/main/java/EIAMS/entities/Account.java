package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;
import javax.validation.constraints.Email;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "active")
    private Integer active;

    @Column(name = "email", nullable = false, length = 64)
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "password", nullable = false, length = 256)
    private String password;

    @Column(name = "role", nullable = false, length = 64)
    private String role;

    @Column(name = "since")
    private Integer since;

    @Column(name = "username", nullable = false, length = 64)
    private String username;

    @OneToMany(mappedBy = "creator")
    private Set<Semester> semesters = new LinkedHashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Token> tokens = new LinkedHashSet<>();

}