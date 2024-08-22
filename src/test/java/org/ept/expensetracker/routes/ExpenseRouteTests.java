package org.ept.expensetracker.routes;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.ept.expensetracker.auth.AuthRequest;
import org.ept.expensetracker.expense.ExpenseDto;
import org.ept.expensetracker.user.Role;
import org.ept.expensetracker.user.User;
import org.ept.expensetracker.user.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseRouteTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeAll
    void setUpAll() {
        insertTestUser();
    }

    @BeforeEach
    void setUp() {
        String token = authenticateUser();
        restTemplate.getRestTemplate().setInterceptors(
                List.of((request, body, execution) -> {
                    request.getHeaders().set("Authorization", "Bearer " + token);
                    return execution.execute(request, body);
                })
        );
    }

    void insertTestUser() {
        AuthRequest user = new AuthRequest();
        user.setEmail("some_email");
        user.setPassword(passwordEncoder.encode("some_password"));
        User newUser = User.builder()
                .id(1L)
                .email(user.getEmail())
                .password(user.getPassword())
                .role(Role.USER)
                .build();
        userService.saveUser(newUser);
    }

    String authenticateUser() {
        AuthRequest user = new AuthRequest();
        user.setEmail("some_email");
        user.setPassword("some_password");
        ResponseEntity<String> response = restTemplate.postForEntity("/auth/login", user, String.class);

        if (response.getBody() == null || response.getBody().isEmpty()) {
            throw new RuntimeException("Failed to authenticate user: response body is null or empty");
        }

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String token = documentContext.read("$.token");
        if (token == null) {
            throw new RuntimeException("Failed to authenticate user: token is null");
        }
        return token;
    }

    @Test
    void shouldReturnAnExpenseWithSpecifiedId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/expenses/10", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(10);
    }

    @Test
    void shouldReturnNotFoundWhenExpenseDoesntExist() {
        ResponseEntity<String> response = restTemplate.getForEntity("/expenses/123", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateNewExpense() {
        ExpenseDto expenseDto = new ExpenseDto(123.45, "FOOD");
        ResponseEntity<String> response = restTemplate
                .postForEntity("/expenses", expenseDto, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-123.45, 0})
    void shouldNotCreateWhenExpenseAmountIsLessThanOrZero(double amount) {
        ExpenseDto expenseDto = new ExpenseDto(amount, "FOOD");
        ResponseEntity<String> response = restTemplate
                .postForEntity("/expenses", expenseDto, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "INVALID"})
    void shouldNotCreateWhenExpenseCategoryIsInvalid(String category) {
        ExpenseDto expenseDto = new ExpenseDto(123.45, category);
        ResponseEntity<String> response = restTemplate
                .postForEntity("/expenses", expenseDto, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnAListOfExpenses() {
        ResponseEntity<String> response = restTemplate.getForEntity("/expenses", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int length = documentContext.read("$.length()");
        assertThat(length).isEqualTo(3);
    }

    @Test
    void shouldReturnAPageWithRequestedNumberAndSize() {
        final int PAGE_SIZE = 2;
        ResponseEntity<String> response = restTemplate
                .getForEntity("/expenses?page=0&size=" + PAGE_SIZE, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int length = documentContext.read("$.length()");
        assertThat(length).isEqualTo(PAGE_SIZE);
    }

    @Test
    void shouldUpdateAnExpense() {
        ExpenseDto expenseDto = new ExpenseDto(123.45, "FOOD");
        ResponseEntity<String> response = restTemplate
                .postForEntity("/expenses", expenseDto, String.class);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");

        ExpenseDto updatedExpenseDto = new ExpenseDto(543.21, "FOOD");
        restTemplate.put("/expenses/" + id, updatedExpenseDto);

        ResponseEntity<String> updatedResponse = restTemplate.getForEntity("/expenses/" + id, String.class);
        DocumentContext updatedDocumentContext = JsonPath.parse(updatedResponse.getBody());
        Number amount = updatedDocumentContext.read("$.amount");

        assertThat(amount).isEqualTo(543.21);
    }

    @Test
    void shouldDeleteExpense() {
        ExpenseDto expenseDto = new ExpenseDto(123.45, "FOOD");
        ResponseEntity<String> response = restTemplate
                .postForEntity("/expenses", expenseDto, String.class);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");

        restTemplate.delete("/expenses/" + id);

        ResponseEntity<String> deletedResponse = restTemplate.getForEntity("/expenses/" + id, String.class);
        assertThat(deletedResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteExpenseWhenItDoesntExist() {
        ResponseEntity<Void> response = restTemplate
                .exchange("/expenses/123", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
