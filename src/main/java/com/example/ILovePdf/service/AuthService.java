package com.example.ILovePdf.service;

import com.example.ILovePdf.dto.LoginRequest;
import com.example.ILovePdf.dto.SignupRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService implements UserDetailsService {
    private final Map<String, UserDetails> users = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void signup(SignupRequest request) {
        String username = request.username() == null ? "" : request.username().trim();
        String password = request.password() == null ? "" : request.password().trim();

        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password are required");
        }
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("USER")
                .build();
        users.put(username, user);
    }

    public String login(LoginRequest request) {
        UserDetails user = loadUserByUsername(request.username());
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return jwtService.generateToken(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
