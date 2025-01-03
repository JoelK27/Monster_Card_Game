package at.technikum_wien.app.models;

import lombok.Getter;

public class TradingDeal {
    @Getter
    private String id;
    // Getters
    @Getter
    private Card offeredCard;
    private boolean requiresSpell;
    @Getter
    private ElementType elementTypeRequirement;
    @Getter
    private int minimumDamage;
    @Getter
    private String partnerToken;

    public TradingDeal(String id, Card offeredCard, boolean requiresSpell, ElementType elementTypeRequirement, int minimumDamage, String partnerToken) {
        this.id = id;
        this.offeredCard = offeredCard;
        this.requiresSpell = requiresSpell;
        this.elementTypeRequirement = elementTypeRequirement;
        this.minimumDamage = minimumDamage;
        this.partnerToken = partnerToken;
    }

    public boolean requiresSpell() {
        return requiresSpell;
    }
}

