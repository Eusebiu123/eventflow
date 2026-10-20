package ro.eusebiu.identityservice.service;

import ro.eusebiu.identityservice.dto.response.AuthResponse;
import ro.eusebiu.identityservice.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}