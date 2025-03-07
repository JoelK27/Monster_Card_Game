package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.MonsterCard;
import at.technikum_wien.app.models.SpellCard;
import at.technikum_wien.app.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeckRepository {
    private final UnitOfWork unitOfWork;

    public DeckRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public List<Card> findDeckByUserId(int userId) throws SQLException {
        String sql = "SELECT c.* FROM cards c JOIN user_deck ud ON c.id = ud.card_id WHERE ud.user_id = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            List<Card> deck = new ArrayList<>();
            while (resultSet.next()) {
                deck.add(mapResultSetToCard(resultSet));
            }
            return deck;
        }
    }

    public boolean setDeckForUser(User user) throws SQLException {
        String insertSql = "INSERT INTO user_deck (user_id, card_id) VALUES (?, ?)";
        try (PreparedStatement insertStmt = unitOfWork.prepareStatement(insertSql)) {
            for (Card card : user.getDeck().getCards()) {
                insertStmt.setInt(1, user.getID());
                insertStmt.setObject(2, card.getId());
                insertStmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            throw e;
        }
    }

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
}