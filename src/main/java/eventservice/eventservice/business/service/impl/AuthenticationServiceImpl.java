package eventservice.eventservice.business.service.impl;

import eventservice.eventservice.business.handlers.exceptions.InvalidUsernamePasswordException;
import eventservice.eventservice.business.repository.UserRepository;
import eventservice.eventservice.business.service.AuthenticationService;
import eventservice.eventservice.business.utils.StringConstants;
import eventservice.eventservice.model.AuthenticationDto;
import eventservice.eventservice.model.AuthenticationTokenDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    @Value("${jwt.secret-key}")
    private String secret;

    @Value("${jwt.expiration-milliseconds}")
    private Integer expiration;

    @Override
    public AuthenticationTokenDto authenticate(AuthenticationDto authenticationDto){
        AuthenticationTokenDto authenticationTokenDto = new AuthenticationTokenDto();
        if(checkCredentials(authenticationDto)){
            authenticationTokenDto.setAuthorization(
                    "Bearer " + Jwts.builder()
                    .setSubject(authenticationDto.getUsername())
                    .claim("role", isAdmin(authenticationDto) ? "admin" : "user")
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact()
            );
        } else {
            throw new InvalidUsernamePasswordException();
        }
        return authenticationTokenDto;
    }

    private Boolean checkCredentials(AuthenticationDto authenticationDto){
        String password = userRepository.findByUsername(authenticationDto.getUsername())
                .orElseThrow(InvalidUsernamePasswordException::new).getPassword();
        return authenticationDto.getPassword().equals(password);
    }

    private Boolean isAdmin(AuthenticationDto authenticationDto){
        return userRepository.findByUsername(authenticationDto.getUsername()).get().getRoleEntity().getRole().equals(StringConstants.ADMIN);
    }

}
