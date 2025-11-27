package file;
import route.Route;
import transport.Transport_type;
import repository.Route_repository;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class File_route implements Route_repository {
    private final Path path;

    public File_route(String filePath) {
        this.path = Path.of(filePath);
    }

    public void save(Route route) {
        List<Route> routes = readAll();
        routes.add(route);
        writeAll(routes);
    }
    public void update(Route route) {
        List<Route> routes = readAll();
        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i).getRoute_id().equals(route.getRoute_id())) {
                routes.set(i, route);
                break;
            }
        }
        writeAll(routes);
    }

    public void deleteByID(String route_id) {
        List<Route> routes = readAll();
        routes.removeIf(r -> r.getRoute_id().equals(route_id));
        writeAll(routes);
    }

    public Optional<Route> findByID(String route_id) {
        return readAll().stream()
                .filter(r -> r.getRoute_id().equals(route_id))
                .findFirst();
    }

    public List<Route> findAll() {
        return readAll();
    }

    private List<Route> readAll() {
        List<Route> routes = new ArrayList<>();
        if (!Files.exists(path)) return routes;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 5) continue;
                String route_id = parts[0];
                String from = parts[1];
                String to = parts[2];
                double distance = Double.parseDouble(parts[3]);
                Transport_type type = Transport_type.valueOf(parts[4]);
                routes.add(new Route(route_id, from, to, distance, type));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return routes;
    }

    private void writeAll(List<Route> routes) {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            for (Route r : routes) {
                bw.write(r.getRoute_id() + ";" + r.getFrom() + ";" + r.getTo() + ";" +
                        r.getDistance() + ";" + r.getTransport_type().name());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
