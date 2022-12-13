package eventservice.eventservice.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
public class UserIntegrationTest {

    @Autowired
    MockMvc mockMvc;



}
