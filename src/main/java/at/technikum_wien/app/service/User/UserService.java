package at.technikum_wien.app.service.User;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;
import at.technikum_wien.app.controller.UserController;

public class UserService implements Service {
    private final UserController userController;

    public UserService() {
        this.userController = new UserController();
    }

    @Override
    public Response handleRequest(Request request) {
        // Prüfen, ob GET-Anfrage mit einer ID (z.B. /user/1)
        if (request.getMethod() == Method.GET && request.getPathParts().size() > 1) {
            return this.userController.getUser(request.getPathParts().get(1));
        }
        // Prüfen, ob allgemeine GET-Anfrage für alle Benutzer
        else if (request.getMethod() == Method.GET) {
            //return this.userController.getUsers();
            // Alternativ könntest du hier die Repository-Version verwenden:
            return this.userController.getUsersPerRepository();
        }
        // Prüfen, ob POST-Anfrage für das Hinzufügen eines neuen Benutzers
        else if (request.getMethod() == Method.POST) {
            return this.userController.addUser(request);
        }
        // Prüfen, ob PUT-Anfrage für das Aktualisieren eines Benutzers (z.B. /user/1)
        else if (request.getMethod() == Method.PUT && request.getPathParts().size() > 1) {
            return this.userController.updateUser(request.getPathParts().get(1), request);
        }
        // Prüfen, ob DELETE-Anfrage für das Löschen eines Benutzers (z.B. /user/1)
        else if (request.getMethod() == Method.DELETE && request.getPathParts().size() > 1) {
            return this.userController.deleteUser(request.getPathParts().get(1));
        }

        // Wenn keine der Methoden zutrifft, wird BAD_REQUEST zurückgegeben
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Bad Request\" }"
        );
    }
}
