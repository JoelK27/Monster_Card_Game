package at.technikum_wien.app.models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SpellCard extends Card {
    private String spellEffect;

    public SpellCard(UUID id, String name, int damage, String elementType, String spellEffect) {
        super(id, name, damage, elementType);
        this.spellEffect = spellEffect;
    }

    @Override
    public String getType() {
        return "Spell";
    }
}