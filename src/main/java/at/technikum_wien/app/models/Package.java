package at.technikum_wien.app.models;

import java.util.List;
import java.util.UUID;

public class Package {
    private UUID id;
    private List<Card> cards;

    public Package(List<Card> cards) {
        this.id = UUID.randomUUID(); // Generiere eine neue UUID
        this.cards = cards;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}