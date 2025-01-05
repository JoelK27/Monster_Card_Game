package at.technikum_wien.app.controller;

import at.technikum_wien.app.business.TokenManager;
import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.app.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;

public class SessionController extends Controller {
    private final UserRepository userRepository;
    private final TokenManager tokenManager;

    public SessionController() {
        this.userRepository = new UserRepository(new UnitOfWork());
        this.tokenManager = TokenManager.getInstance();
    }

    // POST /sessions/register => Benutzer registrieren
    public Response register(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            // Deserialisiere den Request-Body in ein User-Objekt
            User registerRequest = this.getObjectMapper().readValue(request.getBody(), User.class);
            String username = registerRequest.getUsername();
            String password = registerRequest.getPassword();

            // Überprüfe, ob der Benutzername bereits existiert
            User existingUser = userRepository.findUserByUsername(username);
            if (existingUser != null) {
                return new Response(
                        HttpStatus.CONFLICT, // 409 - Benutzername bereits vergeben
                        ContentType.JSON,
                        "{ \"message\": \"Username already exists\" }"
                );
            }

            // Erstelle einen neuen Benutzer
            User newUser = new User(username, password);
            userRepository.save(newUser);
            unitOfWork.commitTransaction();

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
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Database Error\" }"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // POST /sessions/login => Benutzer einloggen
    public Response login(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            // Deserialisiere den Request-Body in ein User-Objekt
            User loginRequest = this.getObjectMapper().readValue(request.getBody(), User.class);
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            // Überprüfe, ob der Benutzer existiert
            User user = userRepository.findUserByUsername(username);
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
            String token = tokenManager.generateToken(user);
            tokenManager.storeToken(username, token); // Speichere den Token im TokenManager

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}