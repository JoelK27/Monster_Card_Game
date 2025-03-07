package at.technikum_wien.app.service;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;
import at.technikum_wien.app.controller.TransactionsController;

import java.util.logging.Logger;

public class TransactionsService implements Service {
    private final TransactionsController transactionsController;

    public TransactionsService() {
        this.transactionsController = new TransactionsController();
    }

    @Override
    public Response handleRequest(Request request) {
        // Prüfen, ob POST-Anfrage für das Erwerben eines Pakets
        if (request.getMethod() == Method.POST && request.getPathname().equals("/transactions/packages")) {
            return this.transactionsController.acquirePackage(request);
        }

        // Wenn keine der Methoden zutrifft, wird BAD_REQUEST zurückgegeben
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Bad Request\" }"
        );
    }
}