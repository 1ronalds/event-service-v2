package eventservice.eventservice.web.controller;

import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class UserController {

    private final UserService service;

    @GetMapping("/users/{username}")
    public ResponseEntity<UserDto> findUserDetails(@PathVariable String username){
        return ResponseEntity.ok(service.findUserDetails(username));
    }

    @PostMapping("/users")
    public ResponseEntity<Void> saveUser(@RequestBody UserDto user){

        service.saveUser(user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{user_name}")
    public ResponseEntity<Void> editUser(@RequestBody UserDto user, @PathVariable String username){
        return null;
    }

    @DeleteMapping("/users/{user_name}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username){
        return null;
    }

}
