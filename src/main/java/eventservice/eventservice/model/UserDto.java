package eventservice.eventservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotNull
    @Size(min = 5, max = 20, message="Username has to be 5-20 characters long")
    private String username;

    @NotNull
    @Email(message = "Valid email has to be provided")
    @Size(min = 10, max=50, message = "Email has to be 10-50 characters")
    private String email;

    @NotNull
    @Size(min = 8, max = 20, message="Password has to be 8-20 characters long")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull
    @Size(min = 3, max = 20, message="Name has to be 3-20 characters long")
    private String name;

    @NotNull
    @Size(min = 3, max = 20, message="Surname has to be 3-20 characters long")
    private String surname;

    private RoleDto role;

}
