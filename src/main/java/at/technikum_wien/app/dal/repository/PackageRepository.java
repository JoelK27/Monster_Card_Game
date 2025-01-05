package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.Package;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PackageRepository {
    private final UnitOfWork unitOfWork;

    public PackageRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public void save(Package cardPackage) throws SQLException {
        String sql = "INSERT INTO packages (card1, card2, card3, card4, card5) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setObject(1, cardPackage.getCards().get(0).getId());
            stmt.setObject(2, cardPackage.getCards().get(1).getId());
            stmt.setObject(3, cardPackage.getCards().get(2).getId());
            stmt.setObject(4, cardPackage.getCards().get(3).getId());
            stmt.setObject(5, cardPackage.getCards().get(4).getId());
            stmt.executeUpdate();
        }
    }

    public List<Package> findAll() throws SQLException {
        String sql = "SELECT * FROM packages";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Package> packages = new ArrayList<>();
            while (resultSet.next()) {
                packages.add(mapResultSetToPackage(resultSet));
            }
            return packages;
        }
    }

    private Package mapResultSetToPackage(ResultSet resultSet) throws SQLException {
        List<Card> cards = new ArrayList<>();
        cards.add(new CardRepository(unitOfWork).findById(UUID.fromString(resultSet.getString("card1"))));
        cards.add(new CardRepository(unitOfWork).findById(UUID.fromString(resultSet.getString("card2"))));
        cards.add(new CardRepository(unitOfWork).findById(UUID.fromString(resultSet.getString("card3"))));
        cards.add(new CardRepository(unitOfWork).findById(UUID.fromString(resultSet.getString("card4"))));
        cards.add(new CardRepository(unitOfWork).findById(UUID.fromString(resultSet.getString("card5"))));
        return new Package(cards);
    }
}