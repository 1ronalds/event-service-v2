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
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    @Value("${jwt.secret-key}")
    private String secret;

    @Value("${jwt.expiration-milliseconds}")
    private Integer expiration;

    @Override
    public AuthenticationTokenDto authenticate(AuthenticationDto authenticationDto){
        log.info("authenticate() service method called");
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

    private Boolean checkCredentials(AuthenticationDto authenticationDto) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String passwordHash = userRepository.findByUsername(authenticationDto.getUsername())
                .orElseThrow(InvalidUsernamePasswordException::new).getPassword();
        return bCryptPasswordEncoder.matches(authenticationDto.getPassword(), passwordHash);
    }

    private Boolean isAdmin(AuthenticationDto authenticationDto){
        return userRepository.findByUsername(authenticationDto.getUsername()).get().getRoleEntity().getRole().equals(StringConstants.ADMIN);
    }

}
