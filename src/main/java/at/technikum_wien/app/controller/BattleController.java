package at.technikum_wien.app.controller;

import at.technikum_wien.app.business.BattleArena;
import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

public class BattleController extends Controller {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public BattleController() {

    }

    public Response handleBattle(Request request) {
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
            User player1 = userRepository.findUserByToken(token);
            if (player1 == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Invalid token\" }"
                );
            }

            // Gegner auswählen (z.B. zufälligen Benutzer finden)
            List<User> allUsers = (List<User>) userRepository.findAllUsers();
            User player2 = allUsers.stream()
                    .filter(user -> !user.getUsername().equals(player1.getUsername()))
                    .findAny()
                    .orElse(null);

            if (player2 == null) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\" : \"No available opponents\" }"
                );
            }

            // BattleArena starten
            BattleArena battleArena = new BattleArena(player1, player2);
            User winner = battleArena.startBattle();
            List<String> battleLog = battleArena.getBattleLog();

            // Aktualisiere die Benutzerstatistiken in der Datenbank
            userRepository.update(player1);
            userRepository.update(player2);

            // Ergebnis zusammenstellen
            BattleResult result = new BattleResult(
                    winner != null ? winner.getUsername() : "Draw",
                    battleLog
            );

            String jsonResponse = objectMapper.writeValueAsString(result);

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

    // Hilfsklasse zur Darstellung der Battle-Ergebnisse
    private static class BattleResult {
        public String winner;
        public List<String> battleLog;

        public BattleResult(String winner, List<String> battleLog) {
            this.winner = winner;
            this.battleLog = battleLog;
        }
    }
}