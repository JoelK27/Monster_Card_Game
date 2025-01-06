package at.technikum_wien.app.controller;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.DeckRepository;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

public class DeckController extends Controller {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeckController() {
    }

    public Response getDeck(Request request) {
        String authHeader = request.getHeaderMap().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\" : \"Unauthorized\" }"
            );
        }

        String token = authHeader.replace("Bearer ", "");
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = new UserRepository(unitOfWork);
            User user = userRepository.findUserByToken(token);
            if (user == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Invalid token\" }"
                );
            }

            DeckRepository deckRepository = new DeckRepository(unitOfWork);
            List<Card> deck = deckRepository.findDeckByUserId(user.getID());

            String responseBody = objectMapper.writeValueAsString(deck);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    responseBody
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    /**
     * Methode zum Konfigurieren des Decks eines Benutzers.
     *
     * @param request Die HTTP-Anfrage mit dem JSON-Array der Karten-UUIDs.
     * @return Die HTTP-Antwort mit dem entsprechenden Statuscode und Nachricht.
     */
    public Response configureDeck(Request request) {
        String authHeader = request.getHeaderMap().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\" : \"Unauthorized\" }"
            );
        }

        String token = authHeader.replace("Bearer ", "");
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = new UserRepository(unitOfWork);
            User user = userRepository.findUserByToken(token);
            if (user == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Invalid token\" }"
                );
            }

            // Parse das JSON-Array der Karten-UUIDs
            List<String> cardIdStrings = objectMapper.readValue(
                    request.getBody(),
                    new TypeReference<List<String>>() {}
            );

            // Validierung: Deck muss genau 4 Karten enthalten
            if (cardIdStrings.size() != 4) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\" : \"Deck must contain exactly 4 cards.\" }"
                );
            }

            // Konvertiere die Strings zu UUIDs
            List<UUID> cardIds = cardIdStrings.stream()
                    .map(UUID::fromString)
                    .toList();

            DeckRepository deckRepository = new DeckRepository(unitOfWork);
            boolean success = deckRepository.setDeckForUser(user.getID(), cardIds);

            if (success) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"message\" : \"Deck configured successfully.\" }"
                );
            } else {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\" : \"One or more cards are invalid or do not belong to the user.\" }"
                );
            }
        } catch (IllegalArgumentException e) {
            // Ungültige UUID-Formatierung
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"Invalid UUID format.\" }"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }
}