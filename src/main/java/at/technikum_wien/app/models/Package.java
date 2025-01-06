package at.technikum_wien.app.models;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Package {
    private UUID id;

    private List<Card> cards;

    public Package(List<Card> cards) {
        this.cards = cards;
    }

}
