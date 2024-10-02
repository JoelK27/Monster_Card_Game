package at.technikum_wien;

public class SpellCard extends Card {
    private String spellEffect;

    public SpellCard(String name, int damage, String elementType, String spellEffect) {
        super(name, damage, elementType);
        this.spellEffect = spellEffect;
    }

    public String getSpellEffect() {
        return spellEffect;
    }
}