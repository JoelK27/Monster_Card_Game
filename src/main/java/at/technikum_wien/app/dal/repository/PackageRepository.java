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
        String sql = "INSERT INTO packages (id, card1, card2, card3, card4, card5) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setObject(1, cardPackage.getId(), java.sql.Types.OTHER);
            stmt.setObject(2, cardPackage.getCards().get(0).getId(), java.sql.Types.OTHER);
            stmt.setObject(3, cardPackage.getCards().get(1).getId(), java.sql.Types.OTHER);
            stmt.setObject(4, cardPackage.getCards().get(2).getId(), java.sql.Types.OTHER);
            stmt.setObject(5, cardPackage.getCards().get(3).getId(), java.sql.Types.OTHER);
            stmt.setObject(6, cardPackage.getCards().get(4).getId(), java.sql.Types.OTHER);
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

    public Package findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM packages WHERE id = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setObject(1, id, java.sql.Types.OTHER);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToPackage(resultSet);
            }
            return null;
        }
    }

    public void delete(UUID id) throws SQLException {
        String deleteUserPackagesSql = "DELETE FROM user_packages WHERE package_id = ?";
        try (PreparedStatement deleteUserPackagesStmt = unitOfWork.prepareStatement(deleteUserPackagesSql)) {
            deleteUserPackagesStmt.setObject(1, id, java.sql.Types.OTHER);
            deleteUserPackagesStmt.executeUpdate();
        }

        String deletePackageSql = "DELETE FROM packages WHERE id = ?";
        try (PreparedStatement deletePackageStmt = unitOfWork.prepareStatement(deletePackageSql)) {
            deletePackageStmt.setObject(1, id, java.sql.Types.OTHER);
            deletePackageStmt.executeUpdate();
        }
    }

    private Package mapResultSetToPackage(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString("id"));
        List<Card> cards = new ArrayList<>();
        cards.add(new CardRepository(unitOfWork).findById(UUID.fromString(resultSet.getString("card1"))));
        cards.add(new CardRepository(unitOfWork).findById(UUID.fromString(resultSet.getString("card2"))));
        cards.add(new CardRepository(unitOfWork).findById(UUID.fromString(resultSet.getString("card3"))));
        cards.add(new CardRepository(unitOfWork).findById(UUID.fromString(resultSet.getString("card4"))));
        cards.add(new CardRepository(unitOfWork).findById(UUID.fromString(resultSet.getString("card5"))));

        Package cardPackage = new Package(cards);
        cardPackage.setId(id); // Setze die ID des Pakets

        return cardPackage;
    }
}