package at.technikum_wien.app.service;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;
import at.technikum_wien.app.controller.SessionController;

public class SessionService implements Service {
    private final SessionController sessionController;

    public SessionService() {
        this.sessionController = new SessionController();
    }

    @Override
    public Response handleRequest(Request request) {
        // Prüfen, ob POST-Anfrage für das Einloggen eines Benutzers
        if (request.getMethod() == Method.POST && "/sessions".equals(request.getPathname())) {
            // Prüfen, ob der Body des Requests Anmeldeinformationen enthält
            if (request.getBody().contains("Username") && request.getBody().contains("Password")) {
                return this.sessionController.login(request);
            } else {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\": \"Bad Request: Missing Username or Password\" }"
                );
            }
        }
        // Prüfen, ob POST-Anfrage für das Registrieren eines Benutzers
        else if (request.getMethod() == Method.POST && "/sessions/register".equals(request.getPathname())) {
            return this.sessionController.register(request);
        }

        // Wenn keine der Methoden zutrifft, wird BAD_REQUEST zurückgegeben
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Bad Request\" }"
        );
    }
}