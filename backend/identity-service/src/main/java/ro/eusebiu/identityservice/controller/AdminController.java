package ro.eusebiu.identityservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(Authentication authentication) {
        return Map.of(
                "message", "Welcome admin",
                "user", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }
}