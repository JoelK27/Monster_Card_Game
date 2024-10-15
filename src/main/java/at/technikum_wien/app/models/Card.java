package at.technikum_wien.app.models;

import lombok.Getter;

@Getter
public abstract class Card {
    protected String name;
    protected int damage;
    protected String elementType;

    public Card(String name, int damage, String elementType) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }

}
