package at.technikum_wien.app.service.Deck;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;
import at.technikum_wien.app.controller.DeckController;

public class DeckService implements Service {
    private final DeckController deckController;

    public DeckService() {
        this.deckController = new DeckController();
    }

    @Override
    public Response handleRequest(Request request) {
        // Prüfen, ob allgemeine GET-Anfrage für alle Benutzer
        if (request.getMethod() == Method.GET) {
            return this.deckController.acquireDecks(request);
            // Alternativ könntest du hier die Repository-Version verwenden:
            // return this.userController.getUsersPerRepository();
        }
        // Prüfen, ob PUT-Anfrage für das Aktualisieren eines Benutzers (z.B. /user/1)
        else if (request.getMethod() == Method.PUT) {
            return this.deckController.acquireDecks(request);
        }

        // Wenn keine der Methoden zutrifft, wird BAD_REQUEST zurückgegeben
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Bad Request\" }"
        );
    }
}