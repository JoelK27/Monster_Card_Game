package at.technikum_wien.app.service.User;

import at.technikum_wien.app.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDummyDAL {
    private static UserDummyDAL instance;
    private List<User> users;

    public UserDummyDAL() {
        users = new ArrayList<>();
        users.add(new User("kienboec", "daniel"));
    }

    public static UserDummyDAL getInstance() {
        if (instance == null) {
            instance = new UserDummyDAL();
        }
        return instance;
    }

    // GET /user/:id
    public User getUser(Integer ID) {
        User foundUser = users.stream()
                .filter(user -> ID == user.getID())
                .findAny()
                .orElse(null);

        return foundUser;
    }

    // GET /user
    public List<User> getUsers() {
        return users;
    }

    // POST /user
    public void addUser(User user) {
        users.add(user);
    }

    // PUT /user/:id
    public void updateUser(Integer ID, User updatedUser) {
        users.stream()
                .filter(user -> ID == user.getID())
                .findFirst()
                .ifPresent(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setScore(updatedUser.getScore());
                });
    }

    // DELETE /user/:id
    public void deleteUser(Integer ID) {
        users.removeIf(user -> user.getID() == ID);
    }

    // Methode zum Abrufen eines Benutzers nach Benutzernamen
    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user; // Benutzer gefunden
            }
        }
        return null; // Benutzer nicht gefunden
    }
}
