package at.technikum_wien.app.controller;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.Collection;

public class UserController extends Controller {
    private final UserRepository userRepository;

    public UserController() {
        this.userRepository = new UserRepository(new UnitOfWork());
    }

    // GET /user/:id
    public Response getUser(String id) {
        try {
            User userData = this.userRepository.findUserById(Integer.parseInt(id));
            if (userData == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\": \"User not found\" }"
                );
            }
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

    // GET /user
    public Response getUsers() {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            Collection<User> usersData = this.userRepository.findAllUsers();
            String usersDataJSON = this.getObjectMapper().writeValueAsString(usersData);
            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    usersDataJSON
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

    // POST /user
    public Response addUser(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            User existingUser = this.userRepository.findUserByUsername(user.getUsername());
            if (existingUser != null) {
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.JSON,
                        "{ \"message\": \"User already exists\" }"
                );
            }
            this.userRepository.save(user);
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
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\": \"Internal Server Error\" }"
            );
        }
    }

    // PUT /user/:id
    public Response updateUser(String id, Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            User updatedUser = this.getObjectMapper().readValue(request.getBody(), User.class);
            updatedUser.setID(Integer.parseInt(id));
            this.userRepository.update(updatedUser);
            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"User updated successfully\" }"
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
            e.printStackTrace();
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
            User user = this.userRepository.findUserById(Integer.parseInt(id));
            if (user == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\": \"User not found\" }"
                );
            }
            this.userRepository.delete(user);
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