package at.technikum_wien.models;

import lombok.Getter;

import java.time.LocalDateTime;

public class AuthenticationToken {
    @Getter
    private final String token;
    private final LocalDateTime expiration;
    private final User user;

    public AuthenticationToken(User user) {
        this.user = user;
        this.token = generateToken();
        this.expiration = LocalDateTime.now().plusHours(1);  // Token valid for 1 hour
    }

    private String generateToken() {
        // Placeholder for token generation logic
        return user.getUsername() + "_token_" + System.currentTimeMillis();
    }

    public boolean validateToken() {
        return LocalDateTime.now().isBefore(expiration);
    }

}
