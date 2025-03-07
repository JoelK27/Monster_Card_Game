package at.technikum_wien.app.service;

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
        if (request.getMethod() == Method.GET) {
            return this.tradingsController.getTradingDeals(request);
        } else if (request.getMethod() == Method.POST && request.getPathParts().size() == 1) {
            return this.tradingsController.createTradingDeal(request);
        } else if (request.getMethod() == Method.DELETE && request.getPathParts().size() > 1) {
            return this.tradingsController.deleteTradingDeal(request);
        } else if (request.getMethod() == Method.POST && request.getPathParts().size() > 1) {
            return this.tradingsController.executeTradingDeal(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Bad Request\" }"
        );
    }
}