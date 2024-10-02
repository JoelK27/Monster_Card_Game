package at.technikum_wien;

public abstract class Card {
    protected String name;
    protected int damage;
    protected String elementType;

    public Card(String name, int damage, String elementType) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }

    public int getDamage() {
        return damage;
    }

    public String getName() {
        return name;
    }

    public String getElementType() {
        return elementType;
    }
}
