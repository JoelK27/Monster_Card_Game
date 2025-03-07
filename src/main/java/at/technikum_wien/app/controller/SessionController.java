package at.technikum_wien.app.controller;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.app.models.User;

public class SessionController extends Controller {
    private final UserRepository userRepository;

    public SessionController() {
        this.userRepository = new UserRepository(new UnitOfWork());
    }

    // POST /users => Registrierung
    public Response register(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            User newUser = this.getObjectMapper().readValue(request.getBody(), User.class);
            String token = newUser.getUsername() + "-mtcgToken";
            newUser.setToken(token);
            if (userRepository.findUserByUsername(newUser.getUsername()) != null) {
                return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"message\": \"User already exists\" }"
                );
            }

            userRepository.save(newUser);
            unitOfWork.commitTransaction();
            return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ \"message\": \"User added successfully\" }"
            );
        } catch (Exception e) {
            return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Invalid request body\" }"
            );
        }
    }

    // POST /sessions => Login
    public Response login(Request request) {
        try {
            User loginUser = this.getObjectMapper().readValue(request.getBody(), User.class);
            User existingUser = userRepository.findUserByUsername(loginUser.getUsername());
            if (existingUser == null || !existingUser.getPassword().equals(loginUser.getPassword())) {
                return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Login failed\" }"
                );
            }
            String token = existingUser.getUsername() + "-mtcgToken";
            existingUser.setToken(token);
            userRepository.update(existingUser);
            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"token\": \"" + token + "\" }"
            );
        } catch (Exception e) {
            return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Invalid request body\" }"
            );
        }
    }
}