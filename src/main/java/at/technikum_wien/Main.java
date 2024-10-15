/*Package at.technikum_wien;

import at.technikum_wien.app.models.MonsterCard;
import at.technikum_wien.app.models.ScoreBoard;
import at.technikum_wien.app.models.SpellCard;
import at.technikum_wien.app.models.User;
import at.technikum_wien.app.models.Package;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Create some cards
        MonsterCard card1 = new MonsterCard("Dragon", 50, "Fire", "Dragon");
        SpellCard card2 = new SpellCard("Fireball", 30, "Fire", "Burn");
        MonsterCard card3 = new MonsterCard("Goblin", 10, "Earth", "Goblin");
        SpellCard card4 = new SpellCard("Lightning", 40, "Electric", "Shock");
        MonsterCard card5 = new MonsterCard("Orc", 25, "Earth", "Orc");

        // Create a package of cards
        Package cardPackage = new Package(Arrays.asList(card1, card2, card3, card4, card5));

        // Create two users
        User player1 = new User("Player1", "password123");
        User player2 = new User("Player2", "password456");

        // Player1 acquires the package
        player1.acquirePackage(cardPackage);

        // Player1 selects their best cards for the deck
        player1.selectBestCards();

        // Player1 battles Player2
        player1.battle(player2);

        // Display scoreboard
        ScoreBoard scoreboard = new ScoreBoard(Arrays.asList(player1, player2));
        scoreboard.displayRankings();
    }
}
*/
package at.technikum_wien;

import at.technikum_wien.httpserver.server.Server;
import at.technikum_wien.httpserver.utils.Router;
import at.technikum_wien.app.service.echo.EchoService;
import at.technikum_wien.app.service.UserService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/users", new UserService());
        router.addService("/echo", new EchoService());

        return router;
    }
}

