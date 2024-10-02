package at.technikum_wien;

public class MonsterCard extends Card {
    private String monsterType;

    public MonsterCard(String name, int damage, String elementType, String monsterType) {
        super(name, damage, elementType);
        this.monsterType = monsterType;
    }

    public String getMonsterType() {
        return monsterType;
    }
}

