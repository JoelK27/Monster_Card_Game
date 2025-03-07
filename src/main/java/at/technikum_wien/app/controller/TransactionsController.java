package at.technikum_wien.app.controller;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.dal.repository.PackageRepository;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.Package;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;

import java.sql.SQLException;
import java.util.List;

public class TransactionsController extends Controller {
    private static final int PACKAGE_COST = 5; // Kosten für ein Paket

    public Response acquirePackage(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            String token = request.getHeaderMap().getHeader("Authorization").replace("Bearer ", "");
            UserRepository userRepository = new UserRepository(unitOfWork);
            User user = userRepository.findUserByToken(token);
            if (user == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\": \"Unauthorized\" }"
                );
            }

            // Guthaben überprüfen
            if (user.getCoins() < PACKAGE_COST) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\": \"Not enough money\" }"
                );
            }

            // Paketakquisition fortsetzen...
            PackageRepository packageRepository = new PackageRepository(unitOfWork);
            List<Package> availablePackages = packageRepository.findAll();

            if (availablePackages.isEmpty()) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\": \"No packages available\" }"
                );
            }

            // Paket auswählen (erstes verfügbares Paket)
            Package acquiredPackage = availablePackages.get(0);

            // Überprüfen, ob das Paket eine gültige ID hat
            if (acquiredPackage.getId() == null) {
                return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.JSON,
                        "{ \"message\": \"Package ID is null\" }"
                );
            }

            // Guthaben abziehen und Paket zuweisen
            user.setCoins(user.getCoins() - PACKAGE_COST);
            user.addPackage(acquiredPackage);
            user.getStack().addAll(acquiredPackage.getCards()); // Karten aus dem Paket zum Stack hinzufügen
            userRepository.update(user);

            unitOfWork.commitTransaction();

            // Paket aus der Datenbank entfernen
            packageRepository.delete(acquiredPackage.getId());

            String jsonResponse = "{ \"message\": \"Package acquired successfully\", \"packageId\": \"" + acquiredPackage.getId() + "\" }";

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    jsonResponse
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\": \"Database Error\" }"
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