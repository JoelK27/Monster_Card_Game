package at.technikum_wien.app.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.util.UUID;

@Getter
@JsonDeserialize(using = CardDeserializer.class)
public abstract class Card {
    protected UUID id;
    protected String name;
    protected int damage;
    protected String elementType;

    public Card(UUID id, String name, int damage, String elementType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }

    public abstract String getType();
}