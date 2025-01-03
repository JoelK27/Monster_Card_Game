package at.technikum_wien.app.business;

import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.MonsterCard;
import at.technikum_wien.app.models.SpellCard;
import at.technikum_wien.app.models.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BattleArena {
    private User player1;
    private User player2;
    @Getter
    private User winner;
    private List<String> battleLog = new ArrayList<>();

    public BattleArena(User player1, User player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public User startBattle() {
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

        if (player1.getDeck().getCards().isEmpty()) {
            winner = player2;
        } else if (player2.getDeck().getCards().isEmpty()) {
            winner = player1;
        } else {
            battleLog.add("The battle is a draw.");
            return null;
        }

        updatePlayerStats();
        return winner;
    }

    private Card getRandomCard(List<Card> deck) {
        Collections.shuffle(deck);
        return deck.get(0);
    }

    private boolean isSpecialRule(Card card1, Card card2) {
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

            if (monster1.getMonsterType().equals("Knight") && card2 instanceof SpellCard && ((SpellCard) card2).getSpellEffect().equals("Water")) {
                battleLog.add("Knight drowns instantly due to heavy armor and WaterSpell.");
                return true;
            }

            if (monster1.getMonsterType().equals("Kraken") && card2 instanceof SpellCard) {
                battleLog.add("Kraken is immune to spells.");
                return true;
            }

            if (monster1.getMonsterType().equals("FireElf") && monster2.getMonsterType().equals("Dragon")) {
                battleLog.add("FireElf evades Dragon's attack.");
                return true;
            }
        }
        return false;
    }

    private double calculateDamage(Card attacker, Card defender) {
        double damage = attacker.getDamage();
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
        return damage;
    }

    private void updatePlayerStats() {
        if (winner != null) {
            winner.updateScore(3);
            if (winner == player1) {
                player2.updateScore(-5);
            } else {
                player1.updateScore(-5);
            }
        }
    }

    public List<String> getBattleLog() {
        return battleLog;
    }
}