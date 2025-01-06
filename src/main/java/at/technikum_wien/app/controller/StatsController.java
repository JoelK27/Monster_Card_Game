package at.technikum_wien.app.controller;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StatsController extends Controller {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public StatsController() {

    }

    public Response showStats(Request request) {
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
            User user = userRepository.findUserByToken(token);
            if (user == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Invalid token\" }"
                );
            }

            // Erstelle ein JSON-Objekt mit den gewünschten Statistiken
            String jsonResponse = objectMapper.writeValueAsString(new UserStats(
                    user.getUsername(),
                    user.getScore(),
                    user.getCoins(),
                    user.getDeck().getCards().size()
            ));

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

    // Hilfsklasse zur Darstellung der Benutzerdaten
    private static class UserStats {
        public String username;
        public int score;
        public int coins;
        public int deckSize;

        public UserStats(String username, int score, int coins, int deckSize) {
            this.username = username;
            this.score = score;
            this.coins = coins;
            this.deckSize = deckSize;
        }
    }
}