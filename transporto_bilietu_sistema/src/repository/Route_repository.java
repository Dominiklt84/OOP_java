package repository;
import route.Route;
import java.util.List;
import java.util.Optional;

public interface Route_repository {
    void save(Route route);
    void update(Route route);
    void deleteByID(String route_id);
    Optional<Route> findByID(String route_id);
    List<Route> findAll();
}
