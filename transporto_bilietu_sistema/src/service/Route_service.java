package service;
import route.Route;
import transport.*;
import repository.Route_repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Route_service {
    private final Route_repository route_repository;

    public Route_service(Route_repository route_repository) {
        this.route_repository = route_repository;
    }

    public Route createRoute(String from, String to, double distanceKm, Transport transport) {
        String id = UUID.randomUUID().toString();
        Route r = new Route(id, from, to, distanceKm, transport);
        route_repository.save(r);
        return r;
    }

    public void updateRoute(Route route) {
        route_repository.update(route);
    }

    public void deleteRoute(String id) {
        route_repository.deleteByID(id);
    }

    public List<Route> getAllRoutes() {
        return route_repository.findAll();
    }

    public static Transport createTransportByType(Transport_type type, double pricePerKm) {
        return switch (type) {
            case BUS -> new Bus(pricePerKm);
            case TRAIN -> new Train(pricePerKm);
            case PLANE -> new Plane(pricePerKm);
        };
    }
}
