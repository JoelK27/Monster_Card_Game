package at.technikum_wien.app.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.UUID;

public class CardDeserializer extends JsonDeserializer<Card> {

    @Override
    public Card deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        UUID id = UUID.fromString(node.get("Id").asText());
        String name = node.get("Name").asText();
        int damage = (int) node.get("Damage").asDouble();

        // Ableiten des cardType basierend auf dem Namen
        String cardType;
        if (name.toLowerCase().contains("spell")) {
            cardType = "Spell";
        } else {
            cardType = "Monster";
        }

        if ("Monster".equals(cardType)) {
            return new MonsterCard(id, name, (int) damage, "Unknown", "Unknown");
        } else if ("Spell".equals(cardType)) {
            return new SpellCard(id, name, (int) damage, "Unknown", "Unknown");
        }

        throw new IllegalArgumentException("Unknown card type: " + cardType);
    }
}