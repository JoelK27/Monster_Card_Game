package at.technikum_wien.app.controller;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.CardRepository;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class CardController extends Controller {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Response getCards(Request request) {
        String authHeader = request.getHeaderMap().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Unauthorized\" }"
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
                        "{ \"message\": \"Invalid token\" }"
                );
            }

            CardRepository cardRepository = new CardRepository(unitOfWork);
            List<Card> userCards = cardRepository.findCardsByUserId(user.getID());

            String responseBody = objectMapper.writeValueAsString(userCards);

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
                    "{ \"message\": \"Internal Server Error\" }"
            );
        }
    }
}