package ing.store_management.model.entity;

import ing.store_management.model.enums.CustomerRole;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "users")
@Builder
@ToString
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private CustomerRole role;
}