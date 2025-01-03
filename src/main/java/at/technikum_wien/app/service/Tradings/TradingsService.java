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
        // Prüfen, ob allgemeine GET-Anfrage für alle Handelsdeals
        if (request.getMethod() == Method.GET) {
            return this.tradingsController.getTradingDeals(request);
        }
        // Prüfen, ob POST-Anfrage für das Erstellen eines neuen Handelsdeals
        else if (request.getMethod() == Method.POST && request.getPathParts().size() == 1) {
            return this.tradingsController.createTradingDeal(request);
        }
        // Prüfen, ob POST-Anfrage für das Handeln eines bestehenden Handelsdeals
        else if (request.getMethod() == Method.POST && request.getPathParts().size() > 1) {
            return this.tradingsController.trade(request);
        }
        // Prüfen, ob DELETE-Anfrage für das Löschen eines Handelsdeals
        else if (request.getMethod() == Method.DELETE && request.getPathParts().size() > 1) {
            return this.tradingsController.deleteTradingDeal(request);
        }

        // Wenn keine der Methoden zutrifft, wird BAD_REQUEST zurückgegeben
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Bad Request\" }"
        );
    }
}