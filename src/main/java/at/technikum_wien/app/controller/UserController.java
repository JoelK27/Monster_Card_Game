package at.technikum_wien.app.controller;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Collection;

public class UserController extends Controller {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Response getUser(String username, Request request) {
        String authHeader = request.getHeaderMap().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Unauthorized\" }"
            );
        }

        String token = authHeader.replace("Bearer ", "");
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = new UserRepository(unitOfWork);
            User requestingUser = userRepository.findUserByUsernameAndToken(username, token);
            if (requestingUser == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\": \"Invalid token\" }"
                );
            }

            String userDataJSON = this.getObjectMapper().writeValueAsString(requestingUser);
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
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Database Error\" }"
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

    public Response getUsers(String username, Request request) {
        String authHeader = request.getHeaderMap().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Unauthorized\" }"
            );
        }

        String token = authHeader.replace("Bearer ", "");
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = new UserRepository(unitOfWork);
            User requestingUser = userRepository.findUserByUsernameAndToken(username, token);
            if (requestingUser == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\": \"Invalid token\" }"
                );
            }

            Collection<User> users = userRepository.findAllUsers();
            String usersJSON = this.getObjectMapper().writeValueAsString(users);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    usersJSON
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
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // POST /users => Registrierung
    public Response addUser(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = new UserRepository(unitOfWork);
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            User existingUser = userRepository.findUserByUsername(user.getUsername());
            if (existingUser != null) {
                return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"message\": \"User already exists\" }"
                );
            }
            userRepository.save(user);
            unitOfWork.commitTransaction();
            return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ \"message\": \"User added successfully\" }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Invalid request body\" }"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\": \"Database Error\" }"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // PUT /user/:id
    public Response updateUser(String username, Request request) {
        String authHeader = request.getHeaders().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Unauthorized\" }"
            );
        }

        String token = authHeader.replace("Bearer ", "");
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = new UserRepository(unitOfWork);
            User existingUser = userRepository.findUserByUsernameAndToken(username, token);
            if (existingUser == null) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\": \"Forbidden\" }"
                );
            }

            // JSON einlesen
            User partialUpdates = objectMapper.readValue(request.getBody(), User.class);

            // Nur Felder überschreiben, die im JSON vorhanden sind
            if (partialUpdates.getName() != null) {
                existingUser.setName(partialUpdates.getName());
            }
            if (partialUpdates.getBio() != null) {
                existingUser.setBio(partialUpdates.getBio());
            }
            if (partialUpdates.getImage() != null) {
                existingUser.setImage(partialUpdates.getImage());
            }

            userRepository.update(existingUser);
            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"User updated successfully\" }"
            );
        } catch (JsonProcessingException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\": \"Invalid request body\" }"
            );
        } catch (SQLException e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\": \"Database Error\" }"
            );
        } catch (Exception e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\": \"Internal Server Error\" }"
            );
        }
    }

    // DELETE /user/:id
    public Response deleteUser(String id) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = new UserRepository(unitOfWork);
            User user = userRepository.findUserById(Integer.parseInt(id));
            if (user == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\": \"User not found\" }"
                );
            }
            userRepository.delete(user);
            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"User deleted successfully\" }"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\": \"Database Error\" }"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\": \"Internal Server Error\" }"
            );
        }
    }
}