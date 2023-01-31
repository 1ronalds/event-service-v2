package eventservice.eventservice.web.controller;

import eventservice.eventservice.business.service.AuthenticationService;
import eventservice.eventservice.model.AuthenticationDto;
import eventservice.eventservice.model.AuthenticationTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationTokenDto> authenticateUser(@RequestBody AuthenticationDto authenticationDto){
        return ResponseEntity.ok(authenticationService.authenticate(authenticationDto));
    }
}
