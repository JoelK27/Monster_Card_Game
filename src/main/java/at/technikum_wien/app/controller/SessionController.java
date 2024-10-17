package at.technikum_wien.app.controller;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.app.models.User;
import at.technikum_wien.app.service.User.UserDummyDAL;  // Dummy-DAL für die In-Memory-Speicherung
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public class SessionController extends Controller {
    private final UserDummyDAL userDAL;

    public SessionController() {
        // Dummy-Daten für Tests, stattdessen sollte das Repository-Pattern verwendet werden.
        this.userDAL = UserDummyDAL.getInstance(); // Hole die Singleton-Instanz
    }

    // POST /sessions/login => Benutzer einloggen
    public Response login(Request request) {
        try {
            // Deserialisiere den Request-Body in ein User-Objekt
            User loginRequest = this.getObjectMapper().readValue(request.getBody(), User.class);
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            // Überprüfe, ob der Benutzer existiert
            User user = userDAL.getUserByUsername(username);
            if (user == null) {
                return new Response(
                        HttpStatus.NOT_FOUND, // 404 - Benutzer nicht gefunden
                        ContentType.JSON,
                        "{ \"message\": \"User not found\" }"
                );
            }

            // Überprüfe das Passwort
            if (!user.getPassword().equals(password)) {
                return new Response(
                        HttpStatus.UNAUTHORIZED, // 401 - Ungültige Anmeldedaten
                        ContentType.JSON,
                        "{ \"message\": \"Invalid username or password! Login failed\" }"
                );
            }

            // Wenn Anmeldedaten korrekt sind, generiere ein Token
            String token = generateToken(user);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"token\": \"" + token + "\" }" // Gebe das Token zurück
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // POST /sessions/register => Benutzer registrieren
    public Response register(Request request) {
        try {
            User newUser = this.getObjectMapper().readValue(request.getBody(), User.class);

            // Überprüfen, ob der Benutzer bereits existiert
            List<User> existingUsers = this.userDAL.getUsers();
            for (User existingUser : existingUsers) {
                if (existingUser.getUsername().equals(newUser.getUsername())) {
                    return new Response(
                            HttpStatus.CONFLICT, // 409 - Konflikt
                            ContentType.JSON,
                            "{ \"message\": \"User already exists\" }"
                    );
                }
            }

            // Wenn der Benutzer nicht existiert, füge ihn hinzu
            this.userDAL.addUser(newUser);
            return new Response(
                    HttpStatus.CREATED, // 201 - Benutzer erfolgreich erstellt
                    ContentType.JSON,
                    "{ \"message\": \"User registered successfully\" }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // Hilfsmethode zum Generieren eines Tokens (einfache Implementierung)
    private String generateToken(User user) {
        return user.getUsername() + "-mtcgToken"; // Einfaches Token-Format
    }
}
