package at.technikum_wien.app.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Deck {
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public List<Card> getBestCards() {
        // Placeholder for logic to get the best cards
        return cards.subList(0, Math.min(4, cards.size()));  // Just return first 4 cards for now
    }

}
