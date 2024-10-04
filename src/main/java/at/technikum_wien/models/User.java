package at.technikum_wien.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class User {
    @Getter
    private final String username;
    @Getter
    private final String password;
    private int coins;
    private final List<Card> stack;
    @Getter
    private final Deck deck;
    @Getter
    private int elo;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.coins = 20;  // Starting coins
        this.stack = new ArrayList<>();
        this.deck = new Deck();
        this.elo = 100;   // Starting ELO
    }

    public AuthenticationToken login() {
        // Placeholder for authentication logic
        return new AuthenticationToken(this);
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

    public void updateElo(int points) {
        this.elo += points;
    }

}

