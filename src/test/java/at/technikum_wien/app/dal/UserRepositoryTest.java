package at.technikum_wien.app.dal;

import at.technikum_wien.app.dal.DataAccessException;
import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {
    private UserRepository userRepository;
    private UnitOfWork unitOfWork;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() {
        unitOfWork = mock(UnitOfWork.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        userRepository = new UserRepository(unitOfWork);
    }

    @Test
    public void testFindUserById() throws SQLException {
        when(unitOfWork.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("username")).thenReturn("PlayerOne");
        when(resultSet.getString("password")).thenReturn("password123");
        when(resultSet.getInt("coins")).thenReturn(20);
        when(resultSet.getInt("score")).thenReturn(100);
        when(resultSet.getString("token")).thenReturn("token123");

        User user = userRepository.findUserById(1);

        assertNotNull(user);
        assertEquals(1, user.getID());
        assertEquals("PlayerOne", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals(20, user.getCoins());
        assertEquals(100, user.getScore());
        assertEquals("token123", user.getToken());
    }

    @Test
    public void testFindAllUsers() throws SQLException {
        when(unitOfWork.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("username")).thenReturn("PlayerOne");
        when(resultSet.getString("password")).thenReturn("password123");
        when(resultSet.getInt("coins")).thenReturn(20);
        when(resultSet.getInt("score")).thenReturn(100);
        when(resultSet.getString("token")).thenReturn("token123");

        Collection<User> users = userRepository.findAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        User user = users.iterator().next();
        assertEquals(1, user.getID());
        assertEquals("PlayerOne", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals(20, user.getCoins());
        assertEquals(100, user.getScore());
        assertEquals("token123", user.getToken());
    }

    @Test
    public void testFindUserByIdNotFound() throws SQLException {
        when(unitOfWork.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        User user = userRepository.findUserById(1);

        assertNull(user);
    }

    @Test
    public void testFindUserByIdThrowsException() throws SQLException {
        when(unitOfWork.prepareStatement(anyString())).thenThrow(new SQLException());

        assertThrows(DataAccessException.class, () -> userRepository.findUserById(1));
    }

    @Test
    public void testSaveUser() throws SQLException {
        User user = new User("PlayerOne", "password123");
        when(unitOfWork.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser);
        assertEquals(1, savedUser.getID());
        verify(preparedStatement, times(1)).setString(1, "PlayerOne");
        verify(preparedStatement, times(1)).setString(2, "password123");
        verify(preparedStatement, times(1)).setInt(3, 20);
        verify(preparedStatement, times(1)).setInt(4, 100);
        verify(preparedStatement, times(1)).setString(5, null);
    }

    @Test
    public void testUpdateUser() throws SQLException {
        User user = new User("PlayerOne", "password123");
        user.setID(1);
        user.setCoins(30);
        user.setScore(200);
        user.setToken("token123");

        when(unitOfWork.prepareStatement(anyString())).thenReturn(preparedStatement);

        User updatedUser = userRepository.update(user);

        assertNotNull(updatedUser);
        assertEquals(1, updatedUser.getID());
        verify(preparedStatement, times(1)).setString(1, "PlayerOne");
        verify(preparedStatement, times(1)).setString(2, "password123");
        verify(preparedStatement, times(1)).setInt(3, 30);
        verify(preparedStatement, times(1)).setInt(4, 200);
        verify(preparedStatement, times(1)).setString(5, "token123");
        verify(preparedStatement, times(1)).setInt(6, 1);
    }

    @Test
    public void testDeleteUser() throws SQLException {
        User user = new User("PlayerOne", "password123");
        user.setID(1);

        when(unitOfWork.prepareStatement(anyString())).thenReturn(preparedStatement);

        User deletedUser = userRepository.delete(user);

        assertEquals(1, deletedUser.getID());
        verify(preparedStatement, times(1)).setInt(1, 1);
    }
}