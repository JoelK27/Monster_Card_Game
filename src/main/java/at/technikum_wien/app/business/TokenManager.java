package at.technikum_wien.app.business;

import at.technikum_wien.app.models.User;

import java.util.HashMap;
import java.util.Map;

public class TokenManager {
    private static TokenManager instance;
    private final Map<String, String> tokenStore = new HashMap<>();

    private TokenManager() {
    }

    public static TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    public String generateToken(User user) {
        return user.getUsername() + "-mtcgToken"; // Einfaches Token-Format
    }

    public void storeToken(String username, String token) {
        tokenStore.put(username, token);
    }

    public String getToken(String username) {
        return tokenStore.get(username);
    }

    public boolean isValidToken(String token) {
        return tokenStore.containsValue(token);
    }

    public String getUsernameForToken(String token) {
        for (Map.Entry<String, String> entry : tokenStore.entrySet()) {
            if (entry.getValue().equals(token)) {
                return entry.getKey();
            }
        }
        return null;
    }
}