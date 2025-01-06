package at.technikum_wien.app.models;

import at.technikum_wien.app.business.BattleArena;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
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
    @Getter
    @Setter
    private String Name;
    @Getter
    @Setter
    private String Bio;
    @Getter
    @Setter
    private String Image;
    @Getter
    @Setter
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
    @Getter
    private List<TradingDeal> tradingDeals = new ArrayList<>();
    @Getter
    private List<Package> packages;


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
        this.token  = username + "-mtcgToken";
        this.packages = new ArrayList<>();
    }

    public void acquirePackage(Package cardPackage) {
        if (coins >= 5) {
            coins -= 5;
            stack.addAll(cardPackage.getCards());
        } else {
            System.out.println("Not enough coins.");
        }
    }

    public void addPackage(Package pkg) {
        this.packages.add(pkg);
    }

    public void selectBestCards() {
        // Sortiere die Karten im Stack nach Schaden in absteigender Reihenfolge
        stack.sort(Comparator.comparingInt(Card::getDamage).reversed());
        // Wähle die besten 4 Karten aus
        List<Card> bestCards = stack.subList(0, Math.min(4, stack.size()));
        deck.setCards(bestCards);
    }

    public void battle(User opponent) {
        BattleArena battle = new BattleArena(this, opponent);
        User winner = battle.startBattle();

        // Display battle log
        System.out.println("Battle Log:");
        for (String logEntry : battle.getBattleLog()) {
            System.out.println(logEntry);
        }

        // Display winner
        if (winner != null) {
            System.out.println("The winner is: " + winner.getUsername());
        } else {
            System.out.println("The battle is a draw.");
        }

        // Update player stats
        updatePlayerStats(winner, opponent);
    }

    private void updatePlayerStats(User winner, User opponent) {
        if (winner != null) {
            winner.updateScore(3);
            if (winner == this) {
                opponent.updateScore(-5);
            } else {
                this.updateScore(-5);
            }
        }
    }

    public void tradeCard(Card card, User otherUser) {
        if (stack.contains(card) && !deck.getCards().contains(card)) {
            stack.remove(card);
            otherUser.getStack().add(card);
            System.out.println("Card traded successfully.");
        } else {
            System.out.println("Card cannot be traded. It might be in the deck or not owned by the user.");
        }
    }

    public void updateScore(int points) {
        this.score += points;
    }

    public List<Card> getStack() {
        return stack;
    }

    public void addTradingDeal(TradingDeal tradingDeal) {
        this.tradingDeals.add(tradingDeal);
    }

    public void removeTradingDeal(TradingDeal tradingDeal) {
        this.tradingDeals.remove(tradingDeal);
    }
}