package at.technikum_wien.app.controller;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ScoreboardController extends Controller {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScoreboardController() {

    }

    /**
     * Zeigt das Scoreboard mit allen Benutzern an, sortiert nach Score.
     *
     * @param request Die HTTP-Anfrage.
     * @return Die HTTP-Antwort mit dem Scoreboard.
     */
    public Response displayScoreboard(Request request) {
        String authHeader = request.getHeaders().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\" : \"Unauthorized\" }"
            );
        }

        String token = authHeader.replace("Bearer ", "");
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = new UserRepository(unitOfWork);
            User requestingUser = userRepository.findUserByToken(token);
            if (requestingUser == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Invalid token\" }"
                );
            }

            List<User> users = userRepository.findAllUsersSortedByScore();
            List<ScoreboardEntry> scoreboard = users.stream()
                    .map(user -> new ScoreboardEntry(
                            user.getUsername(),
                            user.getScore(),
                            user.getCoins()
                    ))
                    .toList();

            String jsonResponse = objectMapper.writeValueAsString(scoreboard);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    jsonResponse
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // Hilfsklasse zur Darstellung der Scoreboard-Einträge
    private static class ScoreboardEntry {
        public String username;
        public int score;
        public int coins;

        public ScoreboardEntry(String username, int score, int coins) {
            this.username = username;
            this.score = score;
            this.coins = coins;
        }
    }
}