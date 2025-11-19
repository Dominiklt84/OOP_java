package system;
import ticket.Ticket;
import transport.Transport;
import java.util.ArrayList;
import java.util.List;

public class System_Impl implements Ticket_system {
    private final List<Ticket> tickets=new ArrayList<>();

    public Ticket buyTicket(Transport transport) {
        Ticket t = new Ticket(transport);
        tickets.add(t);
        return t;
    }

    public List<Ticket> getTickets() {
        return new ArrayList<>(tickets);
    }
}
