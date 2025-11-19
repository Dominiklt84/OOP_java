package system;
import ticket.Ticket;
import transport.Transport;
import java.util.List;

public interface Ticket_system {
    Ticket buyTicket(Transport transport);
    List<Ticket> getTickets();
}
