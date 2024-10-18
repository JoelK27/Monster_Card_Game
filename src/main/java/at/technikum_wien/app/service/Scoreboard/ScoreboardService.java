package at.technikum_wien.app.service.Scoreboard;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;
import at.technikum_wien.app.controller.ScoreboardController;

public class ScoreboardService implements Service {
    private final ScoreboardController scoreboardController;

    public ScoreboardService() {
        this.scoreboardController = new ScoreboardController();
    }

    @Override
    public Response handleRequest(Request request) {
        // Prüfen, ob allgemeine GET-Anfrage für alle Benutzer
        if (request.getMethod() == Method.GET) {
            return this.scoreboardController.displayScoreboard(request);
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
