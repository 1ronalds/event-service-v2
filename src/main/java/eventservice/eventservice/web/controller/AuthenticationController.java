package eventservice.eventservice.web.controller;

import eventservice.eventservice.business.service.AuthenticationService;
import eventservice.eventservice.model.AuthenticationDto;
import eventservice.eventservice.model.AuthenticationTokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationTokenDto> authenticateUser(@RequestBody AuthenticationDto authenticationDto){
        log.info("authenticateUser() controller method called");
        return ResponseEntity.ok(authenticationService.authenticate(authenticationDto));
    }
}
