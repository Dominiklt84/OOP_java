package repository;
import ticket.Ticket;
import java.util.List;
import java.util.Optional;

public interface Ticket_repository {
    void save(Ticket ticket);
    void update(Ticket ticket);
    Optional<Ticket> findByID(String ticket_id);
    List<Ticket> findByUserID(String user_id);
}
