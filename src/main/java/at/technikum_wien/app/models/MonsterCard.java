package at.technikum_wien.app.models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MonsterCard extends Card {
    private String monsterType;

    public MonsterCard(UUID id, String name, int damage, String elementType, String monsterType) {
        super(id, name, damage, elementType);
        this.monsterType = monsterType;
    }

    @Override
    public String getType() {
        return "Monster";
    }
}

