package at.technikum_wien;

import java.time.LocalDateTime;

public class AuthenticationToken {
    private String token;
    private LocalDateTime expiration;
    private User user;

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

    public String getToken() {
        return token;
    }
}
