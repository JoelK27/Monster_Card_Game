package at.technikum_wien.httpserver.utils;

import at.technikum_wien.httpserver.server.Service;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private Map<String, Service> serviceRegistry = new HashMap<>();

    public void addService(String route, Service service)
    {
        this.serviceRegistry.put(route, service);
    }

    public void removeService(String route)
    {
        this.serviceRegistry.remove(route);
    }

    public Service resolve(String route)
    {
        return this.serviceRegistry.get(route);
    }
}
