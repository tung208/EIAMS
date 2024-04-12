package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "token", schema = "schedule")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "expired")
    private Integer expired;

    @Column(name = "revoked")
    private Integer revoked;

    @Column(name = "token", length = 128)
    private String token;

    @Column(name = "token_type", length = 64)
    private String tokenType;

    @Column(name = "account_id")
    private Integer accountId;

}