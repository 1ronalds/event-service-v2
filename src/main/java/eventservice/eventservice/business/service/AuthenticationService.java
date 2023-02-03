package eventservice.eventservice.business.service;

import eventservice.eventservice.model.AuthenticationDto;
import eventservice.eventservice.model.AuthenticationTokenDto;

public interface AuthenticationService {
    AuthenticationTokenDto authenticate(AuthenticationDto authenticationDto);
}
