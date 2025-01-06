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

public class DeckRepository {
    private final UnitOfWork unitOfWork;

    public DeckRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Find deck by User ID
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

    // Set or update deck for a user
    public boolean setDeckForUser(int userId, List<UUID> cardIds) throws SQLException {
        // Überprüfen Sie, ob alle Karten existieren und dem Benutzer gehören
        String validationSql = "SELECT uc.card_id FROM user_cards uc WHERE uc.user_id = ? AND uc.card_id = ANY (?)";
        try (PreparedStatement validationStmt = unitOfWork.prepareStatement(validationSql)) {
            validationStmt.setInt(1, userId);
            validationStmt.setArray(2, unitOfWork.getConnection().createArrayOf("uuid", cardIds.toArray()));
            ResultSet rs = validationStmt.executeQuery();

            List<UUID> validCardIds = new ArrayList<>();
            while (rs.next()) {
                validCardIds.add((UUID) rs.getObject("card_id"));
            }

            if (validCardIds.size() != cardIds.size()) {
                // Mindestens eine Karte ist ungültig oder gehört nicht zum Benutzer
                return false;
            }
        }

        // Beginnen Sie eine Transaktion
        unitOfWork.getConnection().setAutoCommit(false);
        try {
            // Löschen Sie das bestehende Deck
            String deleteSql = "DELETE FROM user_deck WHERE user_id = ?";
            try (PreparedStatement deleteStmt = unitOfWork.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, userId);
                deleteStmt.executeUpdate();
            }

            // Fügen Sie die neuen Karten hinzu
            String insertSql = "INSERT INTO user_deck (user_id, card_id) VALUES (?, ?)";
            try (PreparedStatement insertStmt = unitOfWork.prepareStatement(insertSql)) {
                for (UUID cardId : cardIds) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setObject(2, cardId);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            // Commit der Transaktion
            unitOfWork.getConnection().commit();
            return true;
        } catch (SQLException e) {
            // Rollback bei Fehlern
            unitOfWork.getConnection().rollback();
            e.printStackTrace();
            return false;
        } finally {
            unitOfWork.getConnection().setAutoCommit(true);
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
}