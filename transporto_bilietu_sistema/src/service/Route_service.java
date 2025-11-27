package service;
import route.Route;
import transport.Transport_type;
import repository.Route_repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Route_service {
    private final Route_repository route_repository;

    public Route_service(Route_repository route_repository) {
        this.route_repository = route_repository;
    }

    public Route createRoute(String from, String to, double distance, Transport_type transport_type) {
        String route_id = UUID.randomUUID().toString();
        Route route = new Route(route_id, from, to, distance, transport_type);
        route_repository.save(route);
        return route;
    }

    public void updateRoute(Route route) {
        route_repository.update(route);
    }

    public void deleteRoute(String route_id) {
        route_repository.deleteByID(route_id);
    }

    public List<Route> getAllRoutes() {
        return route_repository.findAll();
    }

    public Optional<Route> findByID(String route_id) {
        return route_repository.findByID(route_id);
    }
}
