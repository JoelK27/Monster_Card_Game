package at.technikum_wien.app.service;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;
import at.technikum_wien.app.controller.BattleController;
import at.technikum_wien.app.utils.Producer;
import at.technikum_wien.app.utils.Consumer;

public class BattleService implements Service {
    private final BattleController battleController;
    private final Producer producer;
    private final Consumer consumer;

    public BattleService() {
        this.battleController = new BattleController();
        this.producer = new Producer();
        this.consumer = new Consumer();
    }

    @Override
    public Response handleRequest(Request request) {
        // Prüfen, ob POST-Anfrage für das Starten eines Battles
        if (request.getMethod() == Method.POST) {
            return startBattleAsync(request);
        }

        // Wenn keine der Methoden zutrifft, wird BAD_REQUEST zurückgegeben
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Bad Request\" }"
        );
    }

    private Response startBattleAsync(Request request) {
        producer.produce(() -> {
            Response response = battleController.handleBattle(request);
            consumer.consume(() -> {
                // Hier kannst du die Antwort verarbeiten oder protokollieren
                System.out.println("Battle finished with response: " + response.get());
            });
        });

        return new Response(
                HttpStatus.ACCEPTED,
                ContentType.JSON,
                "{ \"message\": \"Battle started asynchronously\" }"
        );
    }
}