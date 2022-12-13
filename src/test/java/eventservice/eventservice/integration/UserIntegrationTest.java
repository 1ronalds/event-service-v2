package eventservice.eventservice.integration;

import eventservice.eventservice.business.repository.model.RoleEntity;
import eventservice.eventservice.business.repository.model.UserEntity;
import eventservice.eventservice.business.service.UserService;
import eventservice.eventservice.model.RoleDto;
import eventservice.eventservice.model.UserDto;
import eventservice.eventservice.web.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UserIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    UserDto userDto;
    String username;

    @BeforeEach
    void init() {
        RoleDto roleDto = new RoleDto(1L, "admin");
        userDto = new UserDto(1L, "AdminUser", "admin@admin.com", "password123", "Adam", "Leo", roleDto);
        username = "AdminUser";
    }

    @Test
    void findUserDetails() throws Exception {
        mockMvc.perform(post("/v1/users")
                        .content(""))
                .andDo(print());
    }
}
