package at.technikum_wien.app.business;

import at.technikum_wien.app.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BattleArenaTest {

    private User player1;
    private User player2;
    private BattleArena battleArena;

    @BeforeEach
    void setUp() {
        player1 = new User("Player1", "password1");
        player2 = new User("Player2", "password2");

        player1.getDeck().addCard(new MonsterCard("Goblin", 30, "Earth", "Goblin"));
        player1.getDeck().addCard(new SpellCard("Fireball", 50, "Fire", "Fireball"));
        player2.getDeck().addCard(new MonsterCard("Goblin", 30, "Earth", "Goblin"));
        player2.getDeck().addCard(new SpellCard("Fireball", 50, "Fire", "Fireball"));

        battleArena = new BattleArena(player1, player2);
    }

    @Test
    void testBattleInitialization() {
        assertNotNull(battleArena);
        assertEquals(player1, battleArena.getPlayer1());
        assertEquals(player2, battleArena.getPlayer2());
    }

    @Test
    void testBattleWithoutWinner() {
        User winner = battleArena.startBattle();
        assertNull(winner);
    }

    @Test
    void testGoblinAfraidOfDragon() {
        MonsterCard goblin = new MonsterCard("Goblin", 30, "Earth", "Goblin");
        MonsterCard dragon = new MonsterCard("Dragon", 100, "Fire", "Dragon");
        boolean result = battleArena.isSpecialRule(goblin, dragon);
        assertTrue(result);
    }

    @Test
    void testWizzardControlsOrk() {
        MonsterCard wizzard = new MonsterCard("Wizzard", 20, "Water", "Wizzard");
        MonsterCard ork = new MonsterCard("Ork", 50, "Earth", "Ork");
        boolean result = battleArena.isSpecialRule(wizzard, ork);
        assertTrue(result);
    }

    @Test
    void testKnightDrownsFromWaterSpell() {
        MonsterCard knight = new MonsterCard("Knight", 70, "Fire", "Knight");
        SpellCard waterSpell = new SpellCard("Water Splash", 40, "Water", "Water");
        boolean result = battleArena.isSpecialRule(knight, waterSpell);
        assertTrue(result);
    }

    @Test
    void testKrakenImmuneToSpells() {
        MonsterCard kraken = new MonsterCard("Kraken", 90, "Water", "Kraken");
        SpellCard fireSpell = new SpellCard("Fireball", 50, "Fire", "Fireball");
        boolean result = battleArena.isSpecialRule(kraken, fireSpell);
        assertTrue(result);
    }

    @Test
    void testFireElfEvadesDragon() {
        MonsterCard fireElf = new MonsterCard("FireElf", 20, "FireElf", "FireElf");
        MonsterCard dragon = new MonsterCard("Dragon", 100, "Dragon", "Dragon");
        boolean result = battleArena.isSpecialRule(fireElf, dragon);
        assertTrue(result);
    }

    @Test
    void testCalculateDamageEffectiveness() {
        SpellCard waterSpell = new SpellCard("Water Splash", 40, "Water", "Water Splash");
        SpellCard fireSpell = new SpellCard("Fireball", 50, "Fire", "Fireball");
        double damage = battleArena.calculateDamage(waterSpell, fireSpell);
        assertEquals(80, damage);
    }

    @Test
    void testCalculateDamageIneffectiveness() {
        SpellCard fireSpell = new SpellCard("Fireball", 50, "Fire", "Fireball");
        SpellCard waterSpell = new SpellCard("Water Splash", 40, "Water", "Water Splash");
        double damage = battleArena.calculateDamage(fireSpell, waterSpell);
        assertEquals(25, damage);
    }

    @Test
    void testCalculateDamageNeutral() {
        SpellCard normalSpell = new SpellCard("Magic Spark", 40, "Normal", "Magic Spark");
        MonsterCard normalMonster = new MonsterCard("Bear", 50, "Normal", "Bear");
        double damage = battleArena.calculateDamage(normalSpell, normalMonster);
        assertEquals(40, damage);
    }

    @Test
    void testRoundLimit() {
        battleArena.startBattle();
        long roundCount = battleArena.getBattleLog().stream()
                .filter(log -> log.startsWith("Round"))
                .count();
        assertEquals(100, roundCount);
    }

    @Test
    void testDrawNoStatsChange() {
        battleArena.startBattle();
        assertEquals(100, player1.getScore());
        assertEquals(100, player2.getScore());
    }

    @Test
    void testBattleLogContent() {
        battleArena.startBattle();
        assertFalse(battleArena.getBattleLog().isEmpty());
        assertTrue(battleArena.getBattleLog().get(0).contains("Round 1:"));
    }

    @Test
    void testCalculateDamage_DoubleEffective() {
        SpellCard waterSpell = new SpellCard("WaterSpell", 50, "Water", "Drowning");
        MonsterCard fireMonster = new MonsterCard("FireMonster", 50, "Fire", "Monster");
        double damage = battleArena.calculateDamage(waterSpell, fireMonster);

        assertEquals(100, damage, "Water spell should deal double damage to fire monster.");
    }

    @Test
    void testCalculateDamage_HalfEffective() {
        SpellCard fireSpell = new SpellCard("FireSpell", 50, "Fire", "Beam");
        MonsterCard waterMonster = new MonsterCard("WaterMonster", 50, "Water", "Monster");
        double damage = battleArena.calculateDamage(fireSpell, waterMonster);

        assertEquals(25, damage, "Fire spell should deal half damage to water monster.");
    }

    @Test
    void testAddCardToDeck() {
        MonsterCard newCard = new MonsterCard("NewMonster", 60, "Fire", "Monster");
        player1.getDeck().addCard(newCard);
        assertTrue(player1.getDeck().getCards().contains(newCard));
    }

    @Test
    void testRemoveCardFromDeck() {
        MonsterCard cardToRemove = (MonsterCard) player1.getDeck().getCards().get(0);
        player1.getDeck().removeCard(cardToRemove);
        assertFalse(player1.getDeck().getCards().contains(cardToRemove));
    }

    @Test
    void testGetBestCards() {
        List<Card> bestCards = player1.getDeck().getBestCards();
        assertEquals(2, bestCards.size()); // Da wir nur 2 Karten im Deck haben
        assertTrue(bestCards.containsAll(player1.getDeck().getCards()));
    }

    @Test
    void testUpdatePlayerStatsAfterBattle() {
        player1.setScore(100);
        player2.setScore(100);
        battleArena.startBattle();
        if (battleArena.getWinner() == player1) {
            assertEquals(103, player1.getScore());
            assertEquals(95, player2.getScore());
        } else if (battleArena.getWinner() == player2) {
            assertEquals(95, player1.getScore());
            assertEquals(103, player2.getScore());
        } else {
            assertEquals(100, player1.getScore());
            assertEquals(100, player2.getScore());
        }
    }

    @Test
    void testGetBattleLog() {
        battleArena.startBattle();
        List<String> battleLog = battleArena.getBattleLog();
        assertFalse(battleLog.isEmpty());
        assertTrue(battleLog.get(0).startsWith("Round 1:"));
    }


    private List<Card> createDeck(int count, String type, int damage) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cards.add(type.equals("Monster") ?
                    new MonsterCard("Monster" + i, damage, "Normal", "Monster") :
                    new SpellCard("Spell" + i, damage, "Fire", "Explosion"));
        }
        return cards;
    }
}
