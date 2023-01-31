package eventservice.eventservice.model;

import lombok.Data;

@Data
public class AuthenticationDto {
    private String username;
    private String password;
}
