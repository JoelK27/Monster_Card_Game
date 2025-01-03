package at.technikum_wien.app.business;

import at.technikum_wien.app.models.MonsterCard;
import at.technikum_wien.app.models.SpellCard;
import at.technikum_wien.app.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class BattleArenaTest {

    private User player1;
    private User player2;

    @BeforeEach
    public void setUp() {
        player1 = new User("Player1", "password123");
        player2 = new User("Player2", "password456");

        MonsterCard card1 = new MonsterCard("Dragon", 50, "Fire", "Dragon");
        SpellCard card2 = new SpellCard("Fireball", 30, "Fire", "Burn");
        MonsterCard card3 = new MonsterCard("Goblin", 10, "Earth", "Goblin");
        SpellCard card4 = new SpellCard("Lightning", 40, "Electric", "Shock");
        MonsterCard card5 = new MonsterCard("Orc", 25, "Earth", "Orc");

        player1.getDeck().setCards(Arrays.asList(card1, card2, card3, card4, card5));
        player2.getDeck().setCards(Arrays.asList(card1, card2, card3, card4, card5));
    }

    @Test
    public void testStartBattle() {
        BattleArena battleArena = new BattleArena(player1, player2);
        User winner = battleArena.startBattle();

        assertNotNull(winner);
        assertTrue(winner == player1 || winner == player2);
    }

    @Test
    public void testBattleLog() {
        BattleArena battleArena = new BattleArena(player1, player2);
        battleArena.startBattle();

        assertFalse(battleArena.getBattleLog().isEmpty());
    }
}