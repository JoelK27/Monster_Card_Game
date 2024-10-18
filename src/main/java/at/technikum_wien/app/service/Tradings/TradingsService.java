package at.technikum_wien.app.service.Tradings;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;
import at.technikum_wien.app.controller.TradingsController;

public class TradingsService implements Service {
    private final TradingsController tradingsController;

    public TradingsService() {
        this.tradingsController = new TradingsController();
    }

    @Override
    public Response handleRequest(Request request) {
        // Prüfen, ob allgemeine GET-Anfrage für alle Benutzer
        if (request.getMethod() == Method.GET) {
            return this.tradingsController.handleTradings(request);
            // Alternativ könntest du hier die Repository-Version verwenden:
            // return this.userController.getUsersPerRepository();
        }
        // Prüfen, ob POST-Anfrage für das Hinzufügen eines neuen Benutzers
        else if (request.getMethod() == Method.POST) {
            return this.tradingsController.handleTradings(request);
        }
        // Prüfen, ob DELETE-Anfrage für das Löschen eines Benutzers (z.B. /user/1)
        else if (request.getMethod() == Method.DELETE && request.getPathParts().size() > 1) {
            return this.tradingsController.handleTradings(request);
        }

        // Wenn keine der Methoden zutrifft, wird BAD_REQUEST zurückgegeben
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Bad Request\" }"
        );
    }
}
