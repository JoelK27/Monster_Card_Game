package at.technikum_wien.app.service.echo;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;

public class EchoService implements Service {
    @Override
    public Response handleRequest(Request request) {
        return new Response(HttpStatus.OK,
                ContentType.PLAIN_TEXT,
                "Echo-" + request.getBody());
    }
}
