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

            Überprüfen, ob der Benutzer "admin" ist
            if (player1.getUsername().equalsIgnoreCase("admin")) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Admin cannot participate in battles.\" }"
                );
            }
            

            // Gegner auswählen (z.B. zufälligen Benutzer finden)
            List<User> allUsers = (List<User>) userRepository.findAllUsers();
            User player2 = allUsers.stream()
                    .filter(user -> !user.getUsername().equals(player1.getUsername()) && !user.getUsername().equalsIgnoreCase("admin"))
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

            // Display battle log
            System.out.println("Battle Log:");
            for (String logEntry : battleArena.getBattleLog()) {
                System.out.println(logEntry);
            }

            // Display winner
            if (winner != null) {
                System.out.println("The winner is: " + winner.getUsername());
            }

            // Aktualisiere die Benutzerstatistiken in der Datenbank
            userRepository.update(player1);
            userRepository.update(player2);

            // Ergebnis zusammenstellen
            String jsonResponse = objectMapper.writeValueAsString("");

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
}