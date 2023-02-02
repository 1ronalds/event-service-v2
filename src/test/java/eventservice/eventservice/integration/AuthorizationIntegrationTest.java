package eventservice.eventservice.integration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class AuthorizationIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Value("${jwt.secret-key}")
    String secret;

    // Valid token authorization is tested in other integration tests

    @Test
    void expiredTokenAuthentication() throws Exception {
        String jwt = "Bearer " + Jwts.builder()
                .setSubject("AdminUser")
                .claim("role","user")
                .setExpiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        mockMvc.perform(get("/v1/events/user/AdminUser")
                        .header("Authorization", jwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidTokenAuthentication() throws Exception {
        String secretInvalid = "OEZoN3BwUTdDS3JZNjJlRXNlRDc2NU40cnhrZzE1bHZWMlo2b01uZXh0dEV0TzlaQ29pdWpHMDVhRUlxUE9Yb0JqRTMyYUp5bFp1RTlTQUVUSkNGc0tCVmZxWvQyenB4NWFKcVE0dkdwWEFwUHJuNnRleEp5cDZ1WTljYXVZeXd1dUU2ZFNHeGtNenRpR1VBY3FNMldnaTRPNDFwWjB0d1MyejJ6UWpCaG8xb243MUR5dm5Ic0c4aDE0RlFxN2U2Z1diQlR2dFBNUHhza0J1RDNCWGx5ZExrN2puY1YwUnJ0c1hLT2d4REtqaVF3M01oVm5hdW1NZFk1RGhabk9oZA==";
        String jwt = "Bearer " + Jwts.builder()
                .setSubject("AdminUser")
                .claim("role","user")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, secretInvalid)
                .compact();

        mockMvc.perform(get("/v1/events/user/AdminUser")
                        .header("Authorization", jwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}
