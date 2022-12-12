package eventservice.eventservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    public UserDto(String username, String email, String password, String name, String surname){
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }

    private Long id;
    private String username;
    private String email;
    private String password;
    private String name;
    private String surname;
    private RoleDto role;


}
