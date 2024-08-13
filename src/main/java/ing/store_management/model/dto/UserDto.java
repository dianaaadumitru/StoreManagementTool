package ing.store_management.model.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private String username;
    private String password;
    private Long id;
    private String role;
}
