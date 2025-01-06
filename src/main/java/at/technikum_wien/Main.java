package at.technikum_wien;

import at.technikum_wien.app.models.MonsterCard;
import at.technikum_wien.app.models.SpellCard;
import at.technikum_wien.app.models.User;
import at.technikum_wien.app.business.BattleArena;
import at.technikum_wien.httpserver.server.Server;
import at.technikum_wien.httpserver.utils.Router;
import at.technikum_wien.app.service.User.UserService;
import at.technikum_wien.app.service.Session.SessionService;
import at.technikum_wien.app.service.Stats.StatsService;
import at.technikum_wien.app.service.Scoreboard.ScoreboardService;
import at.technikum_wien.app.service.Package.PackageService;
import at.technikum_wien.app.service.Tradings.TradingsService;
import at.technikum_wien.app.service.Transactions.TransactionsService;
import at.technikum_wien.app.service.Card.CardService;
import at.technikum_wien.app.service.Deck.DeckService;
import at.technikum_wien.app.service.Battle.BattleService;
import at.technikum_wien.app.utils.Producer;
import at.technikum_wien.app.utils.Consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

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

        // Start a battle between two users
        startBattle();

        // Shutdown producer nachdem consumer fertig ist
        producer.shutdown();
    }

    private static Router configureRouter() {
        Router router = new Router();
        router.addService("/users", new UserService());
        router.addService("/sessions", new SessionService());
        router.addService("/packages", new PackageService());
        router.addService("/transactions/packages", new TransactionsService()); // Neue Zeile hinzufügen
        router.addService("/cards", new CardService());
        router.addService("/deck", new DeckService());
        router.addService("/stats", new StatsService());
        router.addService("/scoreboard", new ScoreboardService());
        router.addService("/battles", new BattleService());
        router.addService("/tradings", new TradingsService());

        return router;
    }

    private static void startBattle() {
        User player1 = new User("Player1", "password123");
        User player2 = new User("Player2", "password456");

        // Add cards to players' decks
        player1.getDeck().setCards(new ArrayList<>(Arrays.asList(
                new MonsterCard(UUID.randomUUID(),"Dragon", 50, "Fire", "Dragon"),
                new SpellCard(UUID.randomUUID(),"Fireball", 30, "Fire", "Burn"),
                new MonsterCard(UUID.randomUUID(),"Goblin", 10, "Earth", "Goblin"),
                new SpellCard(UUID.randomUUID(),"Lightning", 40, "Electric", "Shock"),
                new MonsterCard(UUID.randomUUID(),"Orc", 25, "Earth", "Orc")
        )));

        player2.getDeck().setCards(new ArrayList<>(Arrays.asList(
                new MonsterCard(UUID.randomUUID(),"Dragon", 50, "Fire", "Dragon"),
                new SpellCard(UUID.randomUUID(),"Fireball", 30, "Fire", "Burn"),
                new MonsterCard(UUID.randomUUID(),"Goblin", 10, "Earth", "Goblin"),
                new SpellCard(UUID.randomUUID(),"Lightning", 40, "Electric", "Shock"),
                new MonsterCard(UUID.randomUUID(),"Orc", 25, "Earth", "Orc")
        )));

        // Start the battle
        BattleArena battleArena = new BattleArena(player1, player2);
        User winner = battleArena.startBattle();

        // Display battle log
        System.out.println("Battle Log:");
        for (String logEntry : battleArena.getBattleLog()) {
            System.out.println(logEntry);
        }

        // Display winner
        if (winner != null) {
            System.out.println("The winner is: " + winner.getUsername());
        } else {
            System.out.println("The battle is a draw.");
        }
    }
}