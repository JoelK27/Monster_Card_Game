package at.technikum_wien.app.controller;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.app.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetUser() {
        Request request = new Request();
        request.setUrlContent("/user/1");

        Response response = userController.getUser("1");

        String responseString = response.get();
        int statusCode = Integer.parseInt(responseString.split(" ")[1]);

        assertEquals(HttpStatus.OK.code, statusCode);
        assertTrue(responseString.contains("PlayerOne"));
    }

    @Test
    public void testGetUsers() {
        Request request = new Request();
        request.setUrlContent("/user");

        Response response = userController.getUsers();

        String responseString = response.get();
        int statusCode = Integer.parseInt(responseString.split(" ")[1]);

        assertEquals(HttpStatus.OK.code, statusCode);

        try {
            String responseBody = responseString.split("\r\n\r\n")[1];
            List<User> users = objectMapper.readValue(responseBody, List.class);
            assertNotNull(users);
            assertFalse(users.isEmpty());
        } catch (Exception e) {
            fail("Exception while parsing response body: " + e.getMessage());
        }
    }

    @Test
    public void testAddUser() {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setUrlContent("/user");
        User newUser = new User("NewUser", "password");
        try {
            request.setBody(objectMapper.writeValueAsString(newUser));
        } catch (Exception e) {
            fail("Exception while setting request body: " + e.getMessage());
        }

        Response response = userController.addUser(request);

        String responseString = response.get();
        int statusCode = Integer.parseInt(responseString.split(" ")[1]);

        assertEquals(HttpStatus.CREATED.code, statusCode);
        assertTrue(responseString.contains("User added successfully"));
    }

    @Test
    public void testUpdateUser() {
        Request request = new Request();
        request.setMethod(Method.PUT);
        request.setUrlContent("/user/1");
        User updatedUser = new User("UpdatedUser", "newpassword");
        try {
            request.setBody(objectMapper.writeValueAsString(updatedUser));
        } catch (Exception e) {
            fail("Exception while setting request body: " + e.getMessage());
        }

        Response response = userController.updateUser("1", request);

        String responseString = response.get();
        int statusCode = Integer.parseInt(responseString.split(" ")[1]);

        assertEquals(HttpStatus.OK.code, statusCode);
        assertTrue(responseString.contains("User updated successfully"));
    }

    @Test
    public void testDeleteUser() {
        Request request = new Request();
        request.setMethod(Method.DELETE);
        request.setUrlContent("/user/1");

        Response response = userController.deleteUser("1");

        String responseString = response.get();
        int statusCode = Integer.parseInt(responseString.split(" ")[1]);

        assertEquals(HttpStatus.OK.code, statusCode);
        assertTrue(responseString.contains("User deleted successfully"));
    }
}