package at.technikum_wien.app.business;

import at.technikum_wien.app.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BattleArenaTest {

    private User player1;
    private User player2;
    private BattleArena battleArena;

    @BeforeEach
    void setUp() {
        player1 = new User("Player1", "password1");
        player2 = new User("Player2", "password2");

        // Zwei einfache Karten für jeden Spieler
        player1.getDeck().addCard(new MonsterCard(UUID.randomUUID(), "Goblin", 30 ,"Earth", "Goblin"));
        player1.getDeck().addCard(new SpellCard(UUID.randomUUID(),"Fireball", 50, "Fire", "Fireball"));
        player2.getDeck().addCard(new MonsterCard(UUID.randomUUID(),"Goblin", 30, "Earth", "Goblin"));
        player2.getDeck().addCard(new SpellCard(UUID.randomUUID(),"Fireball", 50, "Fire", "Fireball"));

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
        battleArena.setRandom(new Random() {
            @Override
            public double nextDouble() {
                return 0.3; // Kein Critical Hit
            }
        });
        User winner = battleArena.startBattle();
        assertNull(winner);
    }

    @Test
    void testGoblinAfraidOfDragon() {
        MonsterCard goblin = new MonsterCard(UUID.randomUUID(),"Goblin", 30, "Earth", "Goblin");
        MonsterCard dragon = new MonsterCard(UUID.randomUUID(),"Dragon", 100, "Fire", "Dragon");
        boolean result = battleArena.isSpecialRule(goblin, dragon);
        assertTrue(result);
    }

    @Test
    void testWizzardControlsOrk() {
        MonsterCard wizzard = new MonsterCard(UUID.randomUUID(),"Wizzard", 20, "Water", "Wizzard");
        MonsterCard ork = new MonsterCard(UUID.randomUUID(),"Ork", 50, "Earth", "Ork");
        boolean result = battleArena.isSpecialRule(wizzard, ork);
        assertTrue(result);
    }

    @Test
    void testKnightDrownsFromWaterSpell() {
        MonsterCard knight = new MonsterCard(UUID.randomUUID(),"Knight", 70, "Fire", "Knight");
        SpellCard waterSpell = new SpellCard(UUID.randomUUID(),"Water Splash", 40, "Water", "Water");
        boolean result = battleArena.isSpecialRule(knight, waterSpell);
        assertTrue(result);
    }

    @Test
    void testKrakenImmuneToSpells() {
        MonsterCard kraken = new MonsterCard(UUID.randomUUID(),"Kraken", 90, "Water", "Kraken");
        SpellCard fireSpell = new SpellCard(UUID.randomUUID(),"Fireball", 50, "Fire", "Fireball");
        boolean result = battleArena.isSpecialRule(kraken, fireSpell);
        assertTrue(result);
    }

    @Test
    void testFireElfEvadesDragon() {
        MonsterCard fireElf = new MonsterCard(UUID.randomUUID(),"FireElf", 20, "FireElf", "FireElf");
        MonsterCard dragon = new MonsterCard(UUID.randomUUID(),"Dragon", 100, "Dragon", "Dragon");
        boolean result = battleArena.isSpecialRule(fireElf, dragon);
        assertTrue(result);
    }

    @Test
    void testCalculateDamageEffectiveness() {
        SpellCard waterSpell = new SpellCard(UUID.randomUUID(),"Water Splash", 40, "Water", "Water Splash");
        SpellCard fireSpell = new SpellCard(UUID.randomUUID(),"Fireball", 50, "Fire", "Fireball");
        double damage = battleArena.calculateDamage(waterSpell, fireSpell);
        assertEquals(80, damage);
    }

    @Test
    void testCalculateDamageIneffectiveness() {
        SpellCard fireSpell = new SpellCard(UUID.randomUUID(),"Fireball", 50, "Fire", "Fireball");
        SpellCard waterSpell = new SpellCard(UUID.randomUUID(),"Water Splash", 40, "Water", "Water Splash");
        double damage = battleArena.calculateDamage(fireSpell, waterSpell);
        assertEquals(25, damage);
    }

    @Test
    void testCalculateDamageNeutral() {
        battleArena.setRandom(new Random() {
            @Override
            public double nextDouble() {
                return 0.3; // Kein Critical Hit
            }
        });
        SpellCard normalSpell = new SpellCard(UUID.randomUUID(),"Magic Spark", 40, "Normal", "Magic Spark");
        MonsterCard normalMonster = new MonsterCard(UUID.randomUUID(),"Bear", 50, "Normal", "Bear");
        double damage = battleArena.calculateDamage(normalSpell, normalMonster);
        assertEquals(40, damage);
    }

    @Test
    void testRoundLimitExactScenario() {
        // Decks so gestalten, dass das Battle erst in der 100. Runde entschieden wird
        player1.getDeck().setCards(createDeck(5, "Monster", 5));
        player2.getDeck().setCards(createDeck(5, "Monster", 5));

        battleArena.setRandom(new Random() {
            @Override
            public double nextDouble() {
                return 0.9; // kein Critical
            }
        });

        battleArena.startBattle();

        // Überprüfung, ob Runde 100 noch gespielt wurde, aber keine Runde 101
        long totalRounds = battleArena.getBattleLog().stream()
                .filter(line -> line.startsWith("Round"))
                .count();
        assertEquals(100, totalRounds);
    }

    @Test
    void testDrawNoStatsChange() {
        battleArena.setRandom(new Random() {
            @Override
            public double nextDouble() {
                return 0.3;
            }
        });
        battleArena.startBattle();
        assertEquals(100, player1.getScore());
        assertEquals(100, player2.getScore());
    }

    @Test
    void testBattleLogContent() {
        battleArena.startBattle();
        assertFalse(battleArena.getBattleLog().isEmpty());
    }

    @Test
    void testAddCardToDeck() {
        MonsterCard newCard = new MonsterCard(UUID.randomUUID(),"NewMonster", 60, "Fire", "Monster");
        player1.getDeck().addCard(newCard);
        assertTrue(player1.getDeck().getCards().contains(newCard));
    }

    @Test
    void testRemoveCardFromDeck() {
        if (player1.getDeck().getCards().isEmpty()) {
            player1.getDeck().addCard(new MonsterCard(UUID.randomUUID(), "Goblin", 30, "Earth", "Goblin"));
        }
        Card cardToRemove = player1.getDeck().getCards().get(0);
        player1.getDeck().removeCard(cardToRemove);
        assertFalse(player1.getDeck().getCards().contains(cardToRemove));
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
    void testCriticalHitOccurs() {
        BattleArena arenaWithMockedRandom = new BattleArena(player1, player2);
        arenaWithMockedRandom.setRandom(new Random() {
            @Override
            public double nextDouble() {
                return 0.1; // Immer < 0.2 => Critical Hit
            }
        });
        Card attacker = new MonsterCard(UUID.randomUUID(), "Dragon", 50, "Fire", "Dragon");
        Card defender = new MonsterCard(UUID.randomUUID(), "Goblin", 30, "Earth", "Goblin");
        double damage = arenaWithMockedRandom.calculateDamage(attacker, defender);
        assertEquals(100, damage);
        assertTrue(arenaWithMockedRandom.getBattleLog().contains("Dragon dealt a Critical Hit!"));
    }

    @Test
    void testCriticalHitDoesNotOccur() {
        BattleArena arenaWithMockedRandom = new BattleArena(player1, player2);
        arenaWithMockedRandom.setRandom(new Random() {
            @Override
            public double nextDouble() {
                return 0.3;
            }
        });
        Card attacker = new MonsterCard(UUID.randomUUID(), "Dragon", 50, "Fire", "Dragon");
        Card defender = new MonsterCard(UUID.randomUUID(), "Goblin", 30, "Earth", "Goblin");
        double damage = arenaWithMockedRandom.calculateDamage(attacker, defender);
        assertEquals(50, damage);
        assertFalse(arenaWithMockedRandom.getBattleLog().contains("Dragon erzielt einen Critical Hit!"));
    }

    @Test
    void testBattleWithEmptyDeckForPlayer2() {
        // Spieler2 hat keine Karten
        player2.getDeck().getCards().clear();
        User winner = battleArena.startBattle();
        // Normalerweise gewinnt Player1 sofort
        assertNull(winner);
    }

    @Test
    void testHighDamageCardWinsImmediately() {
        player1.getDeck().getCards().clear();
        player2.getDeck().getCards().clear();
        // Player1 bekommt eine sehr starke Karte
        player1.getDeck().addCard(new MonsterCard(UUID.randomUUID(),"UltraDragon", 999, "Fire", "Dragon"));
        // Player2 bekommt eine schwache Karte
        player2.getDeck().addCard(new MonsterCard(UUID.randomUUID(),"Rat", 1, "Normal", "Rat"));
        User winner = battleArena.startBattle();
        assertEquals(player1, winner);
    }

    @Test
    void testMultipleDrawRounds() {
        // Mehrere Goblins mit gleichem Damage => häufiges Draw
        player1.getDeck().getCards().clear();
        player2.getDeck().getCards().clear();
        for(int i = 0; i < 4; i++) {
            player1.getDeck().addCard(new MonsterCard(UUID.randomUUID(),"Goblin", 10, "Normal", "Goblin"));
            player2.getDeck().addCard(new MonsterCard(UUID.randomUUID(),"Goblin", 10, "Normal", "Goblin"));
        }
        battleArena.setRandom(new Random() {
            @Override
            public double nextDouble() {
                return 0.9; // kein Critical
            }
        });
        battleArena.startBattle();
        // Prüfen, dass im Log Draw-Einträge vorkommen
        assertTrue(battleArena.getBattleLog().stream().anyMatch(entry -> entry.contains("The round is a draw.")));
    }

    // Hilfsfunktion zum Erstellen dynamischer Decks
    private List<Card> createDeck(int count, String type, int damage) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cards.add(type.equals("Monster")
                    ? new MonsterCard(UUID.randomUUID(),"Monster" + i, damage, "Normal", "Monster")
                    : new SpellCard(UUID.randomUUID(),"Spell" + i, damage, "Fire", "Explosion")
            );
        }
        return cards;
    }
}