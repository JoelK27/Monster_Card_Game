package at.technikum_wien.app.models;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.CardRepository;
import at.technikum_wien.app.dal.repository.DeckRepository;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class User {
    @Getter
    @Setter
    private Integer ID;
    private static int idCounter = 1;  // Statische Variable zur Verwaltung der IDs
    @Getter
    @Setter
    @JsonProperty("Username")
    private String username;
    @Getter
    @Setter
    @JsonProperty("Password")
    private String password;
    @Getter
    @Setter
    @JsonProperty("Name")
    private String name;
    @Getter
    @Setter
    @JsonProperty("Bio")
    private String bio;
    @Getter
    @Setter
    @JsonProperty("Image")
    private String image;
    @Getter
    @Setter
    private int coins;
    @Setter
    private List<Card> stack;
    @Getter
    @Setter
    private Deck deck;
    @Getter
    @Setter
    private int score;
    @Getter
    @Setter
    private String token;
    @Getter
    private List<TradingDeal> tradingDeals = new ArrayList<>();
    @Getter
    private List<Package> packages;

    @JsonCreator
    public User(@JsonProperty("Username") String username, @JsonProperty("Password") String password) {
        this.ID = idCounter;  // Weist die aktuelle ID zu
        idCounter++;
        this.username = username;
        this.password = password;
        this.coins = 20;  // Starting coins
        this.stack = new ArrayList<>();
        this.deck = new Deck();
        this.score = 100; // Starting Score
        this.token  = username + "-mtcgToken";
        this.packages = new ArrayList<>();
    }

    public void addPackage(Package pkg) {
        this.packages.add(pkg);
    }

    public void selectBestCards() {
        stack.sort(Comparator.comparingInt(Card::getDamage).reversed());
        List<Card> bestCards = stack.subList(0, Math.min(4, stack.size()));
        deck.setCards(bestCards);
    }

    public void tradeCard(Card card, User otherUser) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            CardRepository cardRepository = new CardRepository(unitOfWork);
            DeckRepository deckRepository = new DeckRepository(unitOfWork);

            // Stack und Deck des Benutzers aus der Datenbank laden
            List<Card> userStack = cardRepository.findCardsByUserId(this.ID);
            List<Card> userDeck = deckRepository.findDeckByUserId(this.ID);

            // Überprüfen, ob die Karte im Stack des Benutzers ist und nicht im Deck
            boolean isInStack = userStack.stream().anyMatch(c -> c.getId().equals(card.getId()));
            boolean isInDeck = userDeck.stream().anyMatch(c -> c.getId().equals(card.getId()));

            if (isInStack && !isInDeck) {
                // Karte aus dem Stack des Benutzers entfernen
                userStack.removeIf(c -> c.getId().equals(card.getId()));
                cardRepository.removeCardFromUser(this.ID, card.getId());

                // Karte zum Stack des anderen Benutzers hinzufügen
                List<Card> otherUserStack = cardRepository.findCardsByUserId(otherUser.getID());
                otherUserStack.add(card);
                cardRepository.addCardToUser(otherUser.getID(), card.getId());

                unitOfWork.commitTransaction();
                System.out.println("Card traded successfully.");
            } else {
                System.out.println("Card cannot be traded. It might be in the deck or not owned by the user.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateScore(int points) {
        this.score += points;
    }

    public List<Card> getStack() {
        return stack;
    }

    public void loadUserCards() {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            CardRepository cardRepository = new CardRepository(unitOfWork);
            List<Card> cards = cardRepository.findCardsByUserId(this.ID);
            this.setStack(cards);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}