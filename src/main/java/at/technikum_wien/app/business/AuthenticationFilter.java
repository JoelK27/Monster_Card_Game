package at.technikum_wien.app.business;

import at.technikum_wien.httpserver.server.Request;

public class AuthenticationFilter {
    public static final TokenManager tokenManager = TokenManager.getInstance();

    public static boolean isAuthenticated(Request request) {
        String authHeader = request.getHeaderMap().getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            return tokenManager.isValidToken(token);
        }
        return false;
    }

    public static String getUsername(Request request) {
        String authHeader = request.getHeaderMap().getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return tokenManager.getUsernameForToken(token);
        }
        return null;
    }
}