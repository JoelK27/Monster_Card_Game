package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.DataAccessException;
import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.TradingDeal;
import at.technikum_wien.app.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository implements RepositoryInterface<Integer, User> {
    private final UnitOfWork unitOfWork;
    private final ConcurrentHashMap<Integer, User> userMap = new ConcurrentHashMap<>();

    public UserRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Find user by ID
    public User findUserById(Integer id) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                "SELECT * FROM users WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find user by ID", e);
        }
    }

    // Find user by token
    public User findUserByToken(String token) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                "SELECT * FROM users WHERE token = ?")) {
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find user by token", e);
        }
    }

    // Find user by username
    public User findUserByUsername(String username) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                "SELECT * FROM users WHERE username = ?")) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find user by username", e);
        }
    }

    public List<User> findAllUsersSortedByScore() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY score DESC";

        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen der Benutzerstatistiken", e);
        }

        return users;
    }

    public User findUserByUsernameAndToken(String username, String token) {
        String sql = "SELECT * FROM users WHERE username = ? AND token = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Fehler beim Abrufen des Benutzers mit Token", e);
        }
        return null;
    }

    
    // Find all users
    public Collection<User> findAllUsers() {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                "SELECT * FROM users")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
            return users;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find all users", e);
        }
    }

    @Override
    public User findById(Integer id) {
        return findUserById(id);
    }

    @Override
    public Collection<User> findAll() {
        return findAllUsers();
    }

    // Save new user
    @Override
    public User save(User user) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                "INSERT INTO users (username, password, coins, score, token) VALUES (?, ?, ?, ?, ?) RETURNING id")) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getCoins());
            preparedStatement.setInt(4, user.getScore());
            preparedStatement.setString(5, user.getToken());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user.setID(resultSet.getInt("id"));
                userMap.put(user.getID(), user);
            }
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save user", e);
        }
    }

    // Update user
    @Override
    public User update(User user) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                "UPDATE users SET name = ?, bio = ?, image = ?, coins = ?, score = ?, token = ? WHERE id = ?")) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getBio());
            preparedStatement.setString(3, user.getImage());
            preparedStatement.setInt(4, user.getCoins());
            preparedStatement.setInt(5, user.getScore());
            preparedStatement.setString(6, user.getToken());
            preparedStatement.setInt(7, user.getID());

            preparedStatement.executeUpdate();
            userMap.put(user.getID(), user);
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update user", e);
        }
    }

    // Delete user
    @Override
    public User delete(User user) {
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(
                "DELETE FROM users WHERE id = ?")) {
            preparedStatement.setInt(1, user.getID());
            preparedStatement.executeUpdate();
            return userMap.remove(user.getID());
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete user", e);
        }
    }

    // Helper method to map a ResultSet to a User object
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User(
                resultSet.getString("username"),
                resultSet.getString("password")
        );
        user.setID(resultSet.getInt("id"));
        user.setCoins(resultSet.getInt("coins"));
        user.setScore(resultSet.getInt("score"));
        user.setToken(resultSet.getString("token"));
        return user;
    }
}