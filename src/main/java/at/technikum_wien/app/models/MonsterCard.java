package at.technikum_wien.app.models;

import lombok.Getter;

@Getter
public class MonsterCard extends Card {
    private String monsterType;

    public MonsterCard(String name, int damage, String elementType, String monsterType) {
        super(name, damage, elementType);
        this.monsterType = monsterType;
    }

}

