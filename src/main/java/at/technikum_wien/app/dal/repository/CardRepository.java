package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.MonsterCard;
import at.technikum_wien.app.models.SpellCard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardRepository {
    private final UnitOfWork unitOfWork;

    public CardRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Save a new card
    public void save(Card card) throws SQLException {
        String sql = "INSERT INTO cards (id, name, damage, element_type, card_type, monster_type, spell_effect) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setObject(1, card.getId());
            stmt.setString(2, card.getName());
            stmt.setInt(3, card.getDamage());
            stmt.setString(4, card.getElementType());
            stmt.setString(5, card instanceof MonsterCard ? "Monster" : "Spell");
            stmt.setString(6, card instanceof MonsterCard ? ((MonsterCard) card).getMonsterType() : null);
            stmt.setString(7, card instanceof SpellCard ? ((SpellCard) card).getSpellEffect() : null);
            stmt.executeUpdate();
        }
    }

    // Find all cards by User ID
    public List<Card> findCardsByUserId(int userId) throws SQLException {
        String sql = "SELECT c.* FROM cards c JOIN user_cards uc ON c.id = uc.card_id WHERE uc.user_id = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            List<Card> cards = new ArrayList<>();
            while (resultSet.next()) {
                cards.add(mapResultSetToCard(resultSet));
            }
            return cards;
        }
    }

    // Helper method to map a ResultSet to a Card object
    private Card mapResultSetToCard(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString("id"));
        String name = resultSet.getString("name");
        int damage = resultSet.getInt("damage");
        String elementType = resultSet.getString("element_type");
        String cardType = resultSet.getString("card_type");

        if ("Monster".equals(cardType)) {
            String monsterType = resultSet.getString("monster_type");
            return new MonsterCard(id, name, damage, elementType, monsterType);
        } else {
            String spellEffect = resultSet.getString("spell_effect");
            return new SpellCard(id, name, damage, elementType, spellEffect);
        }
    }

    // Find a card by ID
    public Card findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM cards WHERE id = ?::uuid";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToCard(resultSet);
            }
            return null;
        }
    }

    // Find all cards
    public List<Card> findAll() throws SQLException {
        String sql = "SELECT * FROM cards";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Card> cards = new ArrayList<>();
            while (resultSet.next()) {
                cards.add(mapResultSetToCard(resultSet));
            }
            return cards;
        }
    }

    // Delete a card
    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM cards WHERE id = ?::uuid";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        }
    }

    // Add a card to a user
    public void addCardToUser(int userId, UUID cardId) throws SQLException {
        String sql = "INSERT INTO user_cards (user_id, card_id) VALUES (?, ?)";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setObject(2, cardId);
            stmt.executeUpdate();
        }
    }

    // Remove a card from a user
    public void removeCardFromUser(int userId, UUID cardId) throws SQLException {
        String sql = "DELETE FROM user_cards WHERE user_id = ? AND card_id = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setObject(2, cardId);
            stmt.executeUpdate();
        }
    }
}