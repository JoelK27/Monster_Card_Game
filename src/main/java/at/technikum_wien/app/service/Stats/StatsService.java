package at.technikum_wien.app.service.Stats;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;
import at.technikum_wien.app.controller.StatsController;

public class StatsService implements Service {
    private final StatsController statsController;

    public StatsService() {
        this.statsController = new StatsController();
    }

    @Override
    public Response handleRequest(Request request) {
        // Prüfen, ob allgemeine GET-Anfrage für alle Benutzer
        if (request.getMethod() == Method.GET) {
            return this.statsController.showStats(request);
            // Alternativ könntest du hier die Repository-Version verwenden:
            // return this.userController.getUsersPerRepository();
        }

        // Wenn keine der Methoden zutrifft, wird BAD_REQUEST zurückgegeben
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Bad Request\" }"
        );
    }
}