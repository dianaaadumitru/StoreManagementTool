package ing.store_management.model.entity;

import jakarta.persistence.Entity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "customers")
@ToString
public class Customer extends User {
    private String address;
    private String phoneNumber;
}