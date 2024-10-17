package at.technikum_wien.app.models;

import at.technikum_wien.app.business.BattleArena;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class User {
    @Getter
    @Setter
    private Integer ID;
    private static int idCounter = 1;  // Statische Variable zur Verwaltung der IDs
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    private int coins;
    private List<Card> stack;
    @Getter
    private Deck deck;
    @Getter
    @Setter
    private int score;
    @Getter
    @Setter
    private String token;

    @JsonCreator
    public User(@JsonProperty("Username") String username, @JsonProperty("Password") String password) {
        this.ID = idCounter;  // Weist die aktuelle ID zu
        idCounter++;
        this.username = username;
        this.password = password;
        this.coins = 20;  // Starting coins
        this.stack = new ArrayList<>();
        this.deck = new Deck();
        this.score = 100; // Starting Score
        this.token = null;
    }

    public void acquirePackage(Package cardPackage) {
        if (coins >= 5) {
            coins -= 5;
            stack.addAll(cardPackage.getCards());
        } else {
            System.out.println("Not enough coins.");
        }
    }

    public void selectBestCards() {
        // Placeholder logic for selecting the best cards
        List<Card> bestCards = stack.subList(0, 4);  // Assume first 4 cards are best for simplicity
        deck.setCards(bestCards);
    }

    public void battle(User opponent) {
        BattleArena battle = new BattleArena(this, opponent);
        User winner = battle.startBattle();
        System.out.println("The winner is: " + winner.getUsername());
    }

    public void tradeCard(Card card, User otherUser) {
        // Placeholder for card trading logic
    }

    public void updateScore(int points) {
        this.score += points;
    }
}

