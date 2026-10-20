package ro.eusebiu.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.eusebiu.identityservice.entity.RefreshToken;
import ro.eusebiu.identityservice.entity.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    void deleteByUser(User user);
}