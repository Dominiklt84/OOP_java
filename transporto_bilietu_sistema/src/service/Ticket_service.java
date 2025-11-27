package service;
import route.Route;
import ticket.Ticket;
import ticket.Ticket_status;
import transport.Transport_type;
import repository.Route_repository;
import repository.Ticket_repository;
import java.util.*;

public class Ticket_service {
    private final Ticket_repository ticket_repository;
    private final Route_repository route_repository;
    private final Map<Transport_type, Double> pricePerKm;

    public Ticket_service(Ticket_repository ticket_repository,
                         Route_repository route_repository,
                         Map<Transport_type, Double> initialPrices) {
        this.ticket_repository = ticket_repository;
        this.route_repository = route_repository;
        this.pricePerKm = new EnumMap<>(Transport_type.class);
        this.pricePerKm.putAll(initialPrices);
    }

    public Ticket buyTicket(String user_id, String route_id) {
        Route route = route_repository.findByID(route_id)
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));
        double rate = pricePerKm.getOrDefault(route.getTransport_type(), 0.0);
        double price = rate * route.getDistance();
        Ticket ticket = new Ticket(UUID.randomUUID().toString(), user_id, route_id, price, Ticket_status.ACTIVE);
        ticket_repository.save(ticket);
        return ticket;
    }

    public void returnTicket(String ticket_id, String user_id) {
        Ticket ticket = ticket_repository.findByID(ticket_id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (!ticket.getUser_id().equals(user_id)) {
            throw new IllegalStateException("Cannot return other user's ticket");
        }
        if (ticket.getStatus() == Ticket_status.RETURNED) {
            return;
        }
        ticket.setStatus(Ticket_status.RETURNED);
        ticket_repository.update(ticket);
    }

    public List<Ticket> getUserTickets(String user_id) {
        return ticket_repository.findByUserID(user_id);
    }

    public Map<Transport_type, Double> getPricePerKm() {
        return new EnumMap<>(pricePerKm);
    }

    public void setPricePerKm(Transport_type type, double price) {
        pricePerKm.put(type, price);
    }
}
