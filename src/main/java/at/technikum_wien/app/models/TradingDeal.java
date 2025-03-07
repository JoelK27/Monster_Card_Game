package at.technikum_wien.app.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class TradingDeal {
    @Getter
    private UUID id;
    @Getter
    private UUID cardToTrade;
    @Getter
    private String type;
    @Getter
    private int minimumDamage;
    @Getter
    @Setter
    private int ownerId;

    @JsonCreator
    public TradingDeal(
            @JsonProperty("Id") UUID id,
            @JsonProperty("CardToTrade") UUID cardToTrade,
            @JsonProperty("Type") String type,
            @JsonProperty("MinimumDamage") int minimumDamage,
            @JsonProperty("OwnerId") int ownerId) {
        this.id = id;
        this.cardToTrade = cardToTrade;
        this.type = type;
        this.minimumDamage = minimumDamage;
        this.ownerId = ownerId;
    }
}