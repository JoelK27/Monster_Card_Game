package at.technikum_wien.app.controller;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.CardRepository;
import at.technikum_wien.app.dal.repository.PackageRepository;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.Package;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class PackageController extends Controller {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Response createPackage(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String token = request.getHeaders().getHeader("Authorization").replace("Bearer ", "");
            User user = new UserRepository(unitOfWork).findUserByToken(token);
            if (user == null || !user.getUsername().equals("admin")) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\": \"Unauthorized\" }"
                );
            }

            List<Card> cards = objectMapper.readValue(request.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, Card.class));
            if (cards.size() != 5) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\": \"A package must contain exactly 5 cards\" }"
                );
            }

            CardRepository cardRepository = new CardRepository(unitOfWork);
            for (Card card : cards) {
                cardRepository.save(card);
            }

            Package newPackage = new Package(cards);
            new PackageRepository(unitOfWork).save(newPackage);
            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"message\": \"Package created successfully\" }"
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