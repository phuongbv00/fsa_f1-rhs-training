package fsa.f1rhstraining.security.controller;

import fsa.f1rhstraining.entity.User;
import fsa.f1rhstraining.repository.UserRepository;
import fsa.f1rhstraining.security.core.TokenResolver;
import fsa.f1rhstraining.security.dto.LoginDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final TokenResolver tokenResolver;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, TokenResolver tokenResolver, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenResolver = tokenResolver;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Map<String, Object> authenticate(@RequestBody LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String token = tokenResolver.generate(user.getUsername(), user.getRoles());
        return Map.of("token", token);
    }

    @GetMapping("/seed")
    @Transactional
    public void seed() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setFullName("Admin");
        user.setEmail("admin@localhost");
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(List.of("ADMIN", "USER"));
        userRepository.save(user);

        User user2 = new User();
        user2.setUsername("user");
        user2.setPassword(passwordEncoder.encode("user"));
        user2.setFullName("User");
        user2.setEmail("user@localhost");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setRoles(List.of("USER"));
        userRepository.save(user2);
    }
}
