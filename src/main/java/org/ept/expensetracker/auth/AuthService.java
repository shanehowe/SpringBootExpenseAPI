package org.ept.expensetracker.auth;

import org.ept.expensetracker.config.JwtService;
import org.ept.expensetracker.user.Role;
import org.ept.expensetracker.user.User;
import org.ept.expensetracker.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtService jwtService, UserService userService, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse signup(AuthRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }

        if (request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        User savedUser = userService.saveUser(user);
        return buildResponse(savedUser);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userService.getUserByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }
}
