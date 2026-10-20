package ro.eusebiu.identityservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.eusebiu.identityservice.entity.Role;
import ro.eusebiu.identityservice.entity.User;
import ro.eusebiu.identityservice.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedAdmin();
    }

    private void seedAdmin() {
        String username = "admin";
        String email = "admin@eventflow.com";

        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return;
        }

        User admin = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode("Admin123!"))
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        userRepository.save(admin);
    }
}