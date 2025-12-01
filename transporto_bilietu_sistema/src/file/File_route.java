package file;
import route.Route;
import transport.*;
import repository.Route_repository;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

    public void deleteByID(String id) {
        List<Route> routes = readAll();
        routes.removeIf(r -> r.getRoute_id().equals(id));
        writeAll(routes);
    }

    public Optional<Route> findByID(String id) {
        return readAll().stream().filter(r -> r.getRoute_id().equals(id)).findFirst();
    }

    public List<Route> findAll() {
        return readAll();
    }

    private List<Route> readAll() {
        List<Route> result = new ArrayList<>();
        if (!Files.exists(path)) return result;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length != 6) continue;

                String id = p[0];
                String from = p[1];
                String to = p[2];
                double distance = Double.parseDouble(p[3]);
                Transport_type type = Transport_type.valueOf(p[4]);
                double pricePerKm = Double.parseDouble(p[5]);

                Transport transport = switch (type) {
                    case BUS -> new Bus(pricePerKm);
                    case TRAIN -> new Train(pricePerKm);
                    case PLANE -> new Plane(pricePerKm);
                };

                result.add(new Route(id, from, to, distance, transport));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private void writeAll(List<Route> routes) {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            for (Route r : routes) {
                bw.write(String.join(";",
                        r.getRoute_id(),
                        r.getFrom(),
                        r.getTo(),
                        String.valueOf(r.getDistance()),
                        r.getTransport().getType().name(),
                        String.valueOf(r.getTransport().getPricePerKm())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
