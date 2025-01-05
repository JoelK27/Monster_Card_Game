package at.technikum_wien.app.service.Package;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;
import at.technikum_wien.app.controller.PackageController;

public class PackageService implements Service {
    private final PackageController packageController;

    public PackageService() {
        this.packageController = new PackageController();
    }

    @Override
    public Response handleRequest(Request request) {
        // Prüfen, ob POST-Anfrage für das Erstellen eines neuen Pakets
        if (request.getMethod() == Method.POST) {
            return this.packageController.createPackage(request);
        }
        // Prüfen, ob GET-Anfrage für das Abrufen aller Pakete
        else if (request.getMethod() == Method.GET) {
            return this.packageController.getPackages(request);
        }

        // Wenn keine der Methoden zutrifft, wird BAD_REQUEST zurückgegeben
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\": \"Bad Request\" }"
        );
    }
}