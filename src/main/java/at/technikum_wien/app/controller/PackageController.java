package at.technikum_wien.app.controller;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.Package;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;

import java.util.ArrayList;
import java.util.List;

public class PackageController extends Controller {
    private static final List<Package> packages = new ArrayList<>();

    public Response createPackage(Request request) {
        try {
            UnitOfWork unitOfWork = new UnitOfWork();
            String token = request.getHeaders().getHeader("Authorization").replace("Bearer ", "");
            User user = new UserRepository(unitOfWork).findUserByToken(token);
            if (user == null || !user.getUsername().equals("admin")) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\": \"Unauthorized\" }"
                );
            }

            List<Card> cards = this.getObjectMapper().readValue(request.getBody(), this.getObjectMapper().getTypeFactory().constructCollectionType(List.class, Card.class));
            Package newPackage = new Package(cards);
            packages.add(newPackage);

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
                    "{ \"message\": \"Internal Server Error\" }"
            );
        }
    }

    public List<Package> getPackages() {
        return packages;
    }
}