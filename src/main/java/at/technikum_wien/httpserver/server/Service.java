package at.technikum_wien.httpserver.server;

public interface Service {
    Response handleRequest(Request request);
}
