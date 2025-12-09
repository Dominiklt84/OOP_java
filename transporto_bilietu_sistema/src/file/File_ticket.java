package file;
import ticket.Ticket;
import ticket.Ticket_status;
import repository.Ticket_repository;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class File_ticket implements Ticket_repository {
    private final Path path;

    public File_ticket(String filePath) {
        this.path = Path.of(filePath);
    }

    public void save(Ticket ticket) {
        List<Ticket> tickets = readAll();
        tickets.add(ticket);
        writeAll(tickets);
    }

    public void update(Ticket ticket) {
        List<Ticket> tickets = readAll();
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getTicket_id().equals(ticket.getTicket_id())) {
                tickets.set(i, ticket);
                break;
            }
        }
        writeAll(tickets);
    }

    public Optional<Ticket> findByID(String ticket_id) {
        return readAll().stream().filter(t -> t.getTicket_id().equals(ticket_id)).findFirst();
    }

    public List<Ticket> findByUserID(String user_id) {
        return readAll().stream()
                .filter(t -> t.getUser_id().equals(user_id))
                .collect(Collectors.toList());
    }

    private List<Ticket> readAll() {
        List<Ticket> tickets = new ArrayList<>();
        if (!Files.exists(path)) return tickets;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 5) continue;
                String ticket_id = parts[0];
                String user_id = parts[1];
                String route_id = parts[2];
                double price = Double.parseDouble(parts[3]);
                Ticket_status status = Ticket_status.valueOf(parts[4]);
                tickets.add(new Ticket(ticket_id, user_id, route_id, price, status));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tickets;
    }

    private void writeAll(List<Ticket> tickets) {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            for (Ticket t : tickets) {
                bw.write(t.getTicket_id() + ";" + t.getUser_id() + ";" + t.getRoute_id() + ";" + t.getPrice() + ";" + t.getStatus().name());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
