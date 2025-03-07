package at.technikum_wien;

import at.technikum_wien.httpserver.server.Server;
import at.technikum_wien.httpserver.utils.Router;
import at.technikum_wien.app.service.UserService;
import at.technikum_wien.app.service.SessionService;
import at.technikum_wien.app.service.StatsService;
import at.technikum_wien.app.service.ScoreboardService;
import at.technikum_wien.app.service.PackageService;
import at.technikum_wien.app.service.TradingsService;
import at.technikum_wien.app.service.TransactionsService;
import at.technikum_wien.app.service.CardService;
import at.technikum_wien.app.service.DeckService;
import at.technikum_wien.app.service.BattleService;
import at.technikum_wien.app.utils.Producer;
import at.technikum_wien.app.utils.Consumer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        producer.produce(() -> {
            Server server = new Server(10001, configureRouter());
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Beispiel für eine asynchrone Aufgabe
        consumer.consume(() -> {
            // Hier kann eine Aufgabe asynchron verarbeitet werden
            System.out.println("Asynchrone Aufgabe wird verarbeitet...");
        });

        // Shutdown producer nachdem consumer fertig ist
        producer.shutdown();
    }

    private static Router configureRouter() {
        Router router = new Router();
        router.addService("/users", new UserService());
        router.addService("/sessions", new SessionService());
        router.addService("/packages", new PackageService());
        router.addService("/transactions", new TransactionsService()); // Neue Zeile hinzufügen
        router.addService("/cards", new CardService());
        router.addService("/deck", new DeckService());
        router.addService("/stats", new StatsService());
        router.addService("/scoreboard", new ScoreboardService());
        router.addService("/battles", new BattleService());
        router.addService("/tradings", new TradingsService());

        return router;
    }
}