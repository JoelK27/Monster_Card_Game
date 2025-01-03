package at.technikum_wien.app.controller;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.User;
import at.technikum_wien.app.service.User.UserDummyDAL;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Collection;
import java.util.List;

public class UserController extends Controller {
    private UserDummyDAL userDAL;

    public UserController() {
        // Dummy-Daten für Tests, stattdessen sollte das Repository-Pattern verwendet werden.
        this.userDAL = UserDummyDAL.getInstance(); // Hole die Singleton-Instanz
    }

    // GET /user/:id
    public Response getUser(String id) {
        try {
            User userData = this.userDAL.getUser(Integer.parseInt(id));
            // JSON-Ausgabe für den Benutzer, z.B. "{ \"id\": 1, \"username\": \"PlayerOne\", \"score\": 100 }"
            String userDataJSON = this.getObjectMapper().writeValueAsString(userData);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userDataJSON
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

    // Methode zum Abrufen eines Benutzers basierend auf einem Token (repository-basiert)
    public Response getUserByToken(String token) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (unitOfWork) {
            User usersData = new UserRepository(unitOfWork).findUserByToken(token);
            String usersDataJSON = this.getObjectMapper().writeValueAsString(usersData);
            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    usersDataJSON
            );
        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // GET /user
    public Response getUsers() {
        try {
            List<User> usersData = this.userDAL.getUsers();
            // JSON-Ausgabe für die Liste der Benutzer
            String usersDataJSON = this.getObjectMapper().writeValueAsString(usersData);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    usersDataJSON
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

    // POST /user
    public Response addUser(Request request) {
        try {
            // Deserialisiere den Request-Body in ein User-Objekt
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);

            // Überprüfen, ob der Benutzer bereits existiert
            List<User> existingUsers = this.userDAL.getUsers();
            for (User existingUser : existingUsers) {
                if (existingUser.getUsername().equals(user.getUsername())) {
                    // Benutzer existiert bereits
                    return new Response(
                            HttpStatus.CONFLICT,  // HTTP 409 - Konflikt
                            ContentType.JSON,
                            "{ \"message\": \"User already exists\" }"
                    );
                }
            }

            // Wenn der Benutzer nicht existiert, füge ihn hinzu
            this.userDAL.addUser(user);
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"message\": \"User added successfully\" }"
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


    // GET /user (repository-basiert)
    public Response getUsersPerRepository() {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (unitOfWork) {
            Collection<User> usersData = new UserRepository(unitOfWork).findAllUsers();
            // JSON-Ausgabe für die Liste der Benutzer
            String usersDataJSON = this.getObjectMapper().writeValueAsString(usersData);
            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    usersDataJSON
            );
        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // PUT /user/:id
    public Response updateUser(String id, Request request) {
        try {
            User updatedUser = this.getObjectMapper().readValue(request.getBody(), User.class);
            this.userDAL.updateUser(Integer.parseInt(id), updatedUser);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"User updated successfully\" }"
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

    // DELETE /user/:id
    public Response deleteUser(String id) {
        this.userDAL.deleteUser(Integer.parseInt(id));

        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\": \"User deleted successfully\" }"
        );
    }

}


