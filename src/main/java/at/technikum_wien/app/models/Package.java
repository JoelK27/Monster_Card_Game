package at.technikum_wien.app.models;

import lombok.Getter;

import java.util.List;

@Getter
public class Package {
    private List<Card> cards;

    public Package(List<Card> cards) {
        this.cards = cards;
    }

}
