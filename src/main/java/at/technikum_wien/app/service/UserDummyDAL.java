package at.technikum_wien.app.service;

import at.technikum_wien.app.models.User;

import java.net.IDN;
import java.util.ArrayList;
import java.util.List;

public class UserDummyDAL {
    private List<User> users;

    public UserDummyDAL() {
        users = new ArrayList<>();
        users.add(new User("Joel", "PlayerOne"));
        users.add(new User("Liam", "PlayerTwo"));
        users.add(new User("Pumba", "PlayerThree"));
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
                    user.setElo(updatedUser.getElo());
                });
    }

    // DELETE /user/:id
    public void deleteUser(Integer ID) {
        users.removeIf(user -> user.getID() == ID);
    }
}
