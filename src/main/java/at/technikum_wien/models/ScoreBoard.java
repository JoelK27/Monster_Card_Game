package at.technikum_wien.models;

import java.util.List;

public class ScoreBoard {
    private List<User> players;

    public ScoreBoard(List<User> players) {
        this.players = players;
    }

    public void displayRankings() {
        players.sort((u1, u2) -> u2.getElo() - u1.getElo());
        System.out.println("Scoreboard Rankings:");
        for (User player : players) {
            System.out.println(player.getUsername() + ": " + player.getElo());
        }
    }
}
