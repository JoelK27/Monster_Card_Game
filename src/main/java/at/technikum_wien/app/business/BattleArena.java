package at.technikum_wien.app.business;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.CardRepository;
import at.technikum_wien.app.models.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BattleArena {
    @Getter
    private User player1;
    @Getter
    private User player2;
    @Getter
    private User winner;
    @Getter
    private List<String> battleLog = new ArrayList<>();
    private static final double CRITICAL_HIT_CHANCE = 0.2; // 20% Chance
    @Setter
    private Random random = new Random();

    public BattleArena(User player1, User player2) {
        this.player1 = player1;
        this.player2 = player2;

        // Decks aus der Datenbank laden
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            CardRepository cardRepository = new CardRepository(unitOfWork);

            // Decks für beide Spieler laden
            player1.getDeck().setCards(cardRepository.findCardsByUserId(player1.getID()));
            player2.getDeck().setCards(cardRepository.findCardsByUserId(player2.getID()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User startBattle() {
        if (player1.getDeck().getCards().isEmpty() || player2.getDeck().getCards().isEmpty()) {
            battleLog.add("One or both players have no cards. No battle was executed.");
            return null;
        }

        int round = 0;
        while (round < 100 && !player1.getDeck().getCards().isEmpty() && !player2.getDeck().getCards().isEmpty()) {
            round++;
            battleLog.add("Round " + round + ":");
            Card card1 = getRandomCard(player1.getDeck().getCards());
            Card card2 = getRandomCard(player2.getDeck().getCards());

            battleLog.add(player1.getUsername() + " plays " + card1.getName() + " (" + card1.getDamage() + " damage)");
            battleLog.add(player2.getUsername() + " plays " + card2.getName() + " (" + card2.getDamage() + " damage)");

            if (isSpecialRule(card1, card2)) {
                continue;
            }

            double damage1 = calculateDamage(card1, card2);
            double damage2 = calculateDamage(card2, card1);

            if (damage1 > damage2) {
                battleLog.add(player1.getUsername() + " wins the round.");
                player1.getDeck().addCard(card2);
                player2.getDeck().removeCard(card2);
            } else if (damage2 > damage1) {
                battleLog.add(player2.getUsername() + " wins the round.");
                player2.getDeck().addCard(card1);
                player1.getDeck().removeCard(card1);
            } else {
                battleLog.add("The round is a draw.");
            }
        }

        if (round >= 100) {
            battleLog.add("The battle reached the round limit and is a draw.");
        }

        if (player1.getDeck().getCards().isEmpty()) {
            winner = player2;
        } else if (player2.getDeck().getCards().isEmpty()) {
            winner = player1;
        } else {
            return null;
        }

        updatePlayerStats();
        return winner;
    }

    private Card getRandomCard(List<Card> deck) {
        Collections.shuffle(deck);
        return deck.get(0);
    }

    boolean isSpecialRule(Card card1, Card card2) {
        if (card1 instanceof MonsterCard && card2 instanceof MonsterCard) {
            MonsterCard monster1 = (MonsterCard) card1;
            MonsterCard monster2 = (MonsterCard) card2;

            if (monster1.getMonsterType().equals("Goblin") && monster2.getMonsterType().equals("Dragon")) {
                battleLog.add("Goblin is too afraid to attack Dragon.");
                return true;
            }

            if (monster1.getMonsterType().equals("Wizzard") && monster2.getMonsterType().equals("Ork")) {
                battleLog.add("Wizzard controls Ork and prevents it from attacking.");
                return true;
            }

            if (monster1.getMonsterType().equals("FireElf") && monster2.getMonsterType().equals("Dragon")) {
                battleLog.add("FireElf evades Dragon's attack.");
                return true;
            }
        }

        if (card1 instanceof MonsterCard && card2 instanceof SpellCard) {
            MonsterCard monster1 = (MonsterCard) card1;
            SpellCard spell1 = (SpellCard) card2;

            if (monster1.getMonsterType().equals("Knight") && ((SpellCard) spell1).getSpellEffect().equals("Water")) {
                battleLog.add("Knight drowns instantly due to heavy armor and WaterSpell.");
                return true;
            }

            if (monster1.getMonsterType().equals("Kraken")) {
                battleLog.add("Kraken is immune to spells.");
                return true;
            }
        }
        return false;
    }

    double calculateDamage(Card attacker, Card defender) {
        double damage = attacker.getDamage();
        boolean criticalHit = isCriticalHit();
        if (attacker instanceof SpellCard || defender instanceof SpellCard) {
            if (attacker.getElementType().equals("Water") && defender.getElementType().equals("Fire")) {
                damage *= 2;
            } else if (attacker.getElementType().equals("Fire") && defender.getElementType().equals("Water")) {
                damage /= 2;
            } else if (attacker.getElementType().equals("Fire") && defender.getElementType().equals("Normal")) {
                damage *= 2;
            } else if (attacker.getElementType().equals("Normal") && defender.getElementType().equals("Fire")) {
                damage /= 2;
            } else if (attacker.getElementType().equals("Normal") && defender.getElementType().equals("Water")) {
                damage *= 2;
            } else if (attacker.getElementType().equals("Water") && defender.getElementType().equals("Normal")) {
                damage /= 2;
            }
        }

        if (criticalHit) {
            damage *= 2;
            battleLog.add(attacker.getName() + " dealt a Critical Hit!");
        }
        
        return damage;
    }

    private void updatePlayerStats() {
        // Nur wenn es wirklich einen Sieger gibt werden ELO-Punkte angepasst
        if (winner != null) {
            winner.updateScore(3);
            if (winner == player1) {
                player2.updateScore(-5);
            } else {
                player1.updateScore(-5);
            }
        } else {
            // Bei Rundenlimit entscheiden wir uns für ELO-Draw
            battleLog.add("No ELO change because the battle ended in a draw.");
        }
    }

    private boolean isCriticalHit() {
        return random.nextDouble() < CRITICAL_HIT_CHANCE;
    }
}