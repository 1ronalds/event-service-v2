package eventservice.eventservice.web.controller;

import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.model.RoleDto;
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
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto user){
        user.setRole(new RoleDto(2L, "user"));
        return ResponseEntity.ok(service.saveUser(user));
    }

    @PutMapping("/users/{user_name}")
    public ResponseEntity<Object> editUser(@RequestBody UserDto user, @PathVariable String username){
        user.setRole(new RoleDto(2L, "user"));

        return ResponseEntity.ok(service.editUser(user));
    }

    @DeleteMapping("/users/{user_name}")
    public ResponseEntity<Object> deleteUser(@PathVariable String username){
        return null;
    }

}
