package eventservice.eventservice.web.controller;

import eventservice.eventservice.business.service.AuthenticationService;
import eventservice.eventservice.model.AuthenticationDto;
import eventservice.eventservice.model.AuthenticationTokenDto;
import eventservice.eventservice.swagger.HTTPResponseMessages;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    /**
     * Authenticates user and returns JTW token
     */
    @ApiOperation(value = "Finds all events including private with parameters")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTTPResponseMessages.HTTP_200),
            @ApiResponse(code = 403, message = HTTPResponseMessages.HTTP_403)
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationTokenDto> authenticateUser(@RequestBody AuthenticationDto authenticationDto){
        return ResponseEntity.ok(authenticationService.authenticate(authenticationDto));
    }
}
