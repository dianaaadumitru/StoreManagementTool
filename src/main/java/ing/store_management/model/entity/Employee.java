package ing.store_management.model.entity;

import jakarta.persistence.Entity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "employees")
@ToString
public class Employee extends User {
    private String employeeNumber;
    private String department;
}