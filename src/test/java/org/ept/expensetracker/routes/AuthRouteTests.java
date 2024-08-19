package org.ept.expensetracker.routes;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.ept.expensetracker.auth.AuthRequest;
import org.ept.expensetracker.user.User;
import org.ept.expensetracker.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthRouteTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void testRegisterUserSaves() {
        AuthRequest user = new AuthRequest();
        user.setEmail("email");
        user.setPassword("password");

        ResponseEntity<Void> response = restTemplate
                .postForEntity("/auth/signup", user, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Optional<User> savedUser = userService.getUserByEmail(user.getEmail());
        assertThat(savedUser).isPresent();
    }

    @Test
    void testRegisterUserDoesNotStorePasswordInPlainText() {
        AuthRequest user = new AuthRequest();
        user.setEmail("email");
        user.setPassword("password");

        ResponseEntity<Void> response = restTemplate
                .postForEntity("/auth/signup", user, Void.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Optional<User> savedUser = userService.getUserByEmail(user.getEmail());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getPassword()).isNotEqualTo(user.getPassword());
    }


    @Test
    void testRegisterUserFailsWhenUserAlreadyExists() {
        userService.saveUser(User.builder()
                .email("email")
                .password("password")
                .build());

        AuthRequest user = new AuthRequest();
        user.setEmail("email");
        user.setPassword("password");

        ResponseEntity<String> response = restTemplate
                .postForEntity("/auth/signup", user, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testLoginUserReturnsToken() {
        userService.saveUser(User.builder()
                .email("email")
                .password(passwordEncoder.encode("password"))
                .build());
        AuthRequest user = new AuthRequest();
        user.setEmail("email");
        user.setPassword("password");

        ResponseEntity<String> response = restTemplate
                .postForEntity("/auth/login", user, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        assertThat(Optional.ofNullable(documentContext.read("$.token"))).isNotNull();
    }

    @Test
    void testLoginUserFailsWhenUserDoesNotExist() {
        AuthRequest user = new AuthRequest();
        user.setEmail("email");
        user.setPassword("password");

        ResponseEntity<String> response = restTemplate
                .postForEntity("/auth/login", user, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
