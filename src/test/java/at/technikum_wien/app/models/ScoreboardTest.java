package at.technikum_wien.app.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreBoardTest {

    private List<User> players;
    private ScoreBoard scoreBoard;

    @BeforeEach
    void setUp() {
        // Beispiel: Drei Spieler mit unterschiedlichen Scores
        User userA = new User("Alice", "passA");
        userA.setScore(50);
        User userB = new User("Bob", "passB");
        userB.setScore(70);
        User userC = new User("Charlie", "passC");
        userC.setScore(30);

        players = new ArrayList<>();
        players.add(userA);
        players.add(userB);
        players.add(userC);

        scoreBoard = new ScoreBoard(players);
    }

    @Test
    void testScoreBoardNotNull() {
        assertNotNull(scoreBoard);
    }

    @Test
    void testDisplayRankingsOrder() {
        // Hier leiten wir die Konsolenausgabe um, um sie überprüfen zu können
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        scoreBoard.displayRankings();

        // Konsolenausgabe wiederherstellen
        System.setOut(originalOut);

        // Prüfen, ob Bob (70) vor Alice (50) und Alice vor Charlie (30) steht
        String output = outContent.toString();
        int bobIndex = output.indexOf("Bob: 70");
        int aliceIndex = output.indexOf("Alice: 50");
        int charlieIndex = output.indexOf("Charlie: 30");

        // Bob sollte zuerst, Alice als zweites und Charlie als drittes erscheinen
        assertTrue(bobIndex < aliceIndex, "Bob sollte vor Alice im Ranking auftauchen");
        assertTrue(aliceIndex < charlieIndex, "Alice sollte vor Charlie im Ranking auftauchen");
    }

    @Test
    void testEmptyScoreBoard() {
        ScoreBoard emptyBoard = new ScoreBoard(new ArrayList<>());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        emptyBoard.displayRankings();

        System.setOut(originalOut);
        String output = outContent.toString();

        // Erwartet wird nur "Scoreboard Rankings:" + Zeilenumbruch
        assertTrue(output.trim().endsWith("Scoreboard Rankings:"),
                "Die Ausgabe sollte nur die Überschrift enthalten, da keine Spieler vorhanden sind.");
    }
}