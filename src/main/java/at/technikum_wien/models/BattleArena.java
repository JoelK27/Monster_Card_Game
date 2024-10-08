package at.technikum_wien.models;

import lombok.Getter;

public class BattleArena {
    private final User player1;
    private final User player2;
    @Getter
    private User winner;

    public BattleArena(User player1, User player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public User startBattle() {
        // Placeholder logic for battle (simple random winner for now)
        if (Math.random() > 0.5) {
            winner = player1;
            player1.updateElo(3);
            player2.updateElo(-5);
        } else {
            winner = player2;
            player2.updateElo(3);
            player1.updateElo(-5);
        }
        return winner;
    }

}
