package at.technikum_wien.app.service.Battle;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;
import at.technikum_wien.app.controller.BattleController;

public class BattleService implements Service {
    private final BattleController battleController;

    public BattleService() {
        this.battleController = new BattleController();
    }

    @Override
    public Response handleRequest(Request request) {
        // Prüfen, ob POST-Anfrage für das Hinzufügen eines neuen Benutzers
        if (request.getMethod() == Method.POST) {
            return this.battleController.handleBattle(request);
        }

        // Wenn keine der Methoden zutrifft, wird BAD_REQUEST zurückgegeben
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Bad Request\" }"
        );
    }
}
