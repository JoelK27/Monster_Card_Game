package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.TradingDeal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TradingDealRepository {
    private final UnitOfWork unitOfWork;

    public TradingDealRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public void save(TradingDeal tradingDeal) throws SQLException {
        String sql = "INSERT INTO trading_deals (id, card_to_trade, type, minimum_damage, owner_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setObject(1, tradingDeal.getId(), java.sql.Types.OTHER);
            stmt.setObject(2, tradingDeal.getCardToTrade(), java.sql.Types.OTHER);
            stmt.setString(3, tradingDeal.getType());
            stmt.setInt(4, tradingDeal.getMinimumDamage());
            stmt.setInt(5, tradingDeal.getOwnerId());
            stmt.executeUpdate();
        }
    }

    public List<TradingDeal> findAll() throws SQLException {
        String sql = "SELECT * FROM trading_deals";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            ResultSet resultSet = stmt.executeQuery();
            List<TradingDeal> tradingDeals = new ArrayList<>();
            while (resultSet.next()) {
                tradingDeals.add(mapResultSetToTradingDeal(resultSet));
            }
            return tradingDeals;
        }
    }

    public TradingDeal findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM trading_deals WHERE id = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setObject(1, id, java.sql.Types.OTHER);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToTradingDeal(resultSet);
            }
            return null;
        }
    }

    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM trading_deals WHERE id = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setObject(1, id, java.sql.Types.OTHER);
            stmt.executeUpdate();
        }
    }

    private TradingDeal mapResultSetToTradingDeal(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString("id"));
        UUID cardToTrade = UUID.fromString(resultSet.getString("card_to_trade"));
        String type = resultSet.getString("type");
        int minimumDamage = resultSet.getInt("minimum_damage");
        int ownerId = resultSet.getInt("owner_id");

        return new TradingDeal(id, cardToTrade, type, minimumDamage, ownerId);
    }
}