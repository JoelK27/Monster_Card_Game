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

import static org.junit.jupiter.api.Assertions.*;

public class SessionControllerTest {

    private SessionController sessionController;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        sessionController = new SessionController();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testRegister() {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setUrlContent("/sessions/register");
        User newUser = new User("NewUser", "password");
        try {
            request.setBody(objectMapper.writeValueAsString(newUser));
        } catch (Exception e) {
            fail("Exception while setting request body: " + e.getMessage());
        }

        Response response = sessionController.register(request);

        String responseString = response.get();
        int statusCode = Integer.parseInt(responseString.split(" ")[1]);

        assertEquals(HttpStatus.CREATED.code, statusCode);
        assertTrue(responseString.contains("User registered successfully"));
    }

    @Test
    public void testLogin() {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setUrlContent("/sessions/login");
        User loginUser = new User("PlayerOne", "password123");
        try {
            request.setBody(objectMapper.writeValueAsString(loginUser));
        } catch (Exception e) {
            fail("Exception while setting request body: " + e.getMessage());
        }

        Response response = sessionController.login(request);

        String responseString = response.get();
        int statusCode = Integer.parseInt(responseString.split(" ")[1]);

        assertEquals(HttpStatus.OK.code, statusCode);
        assertTrue(responseString.contains("token"));
    }
}