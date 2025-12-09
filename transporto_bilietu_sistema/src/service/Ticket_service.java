package service;
import route.Route;
import ticket.Ticket;
import ticket.Ticket_status;
import repository.Route_repository;
import repository.Ticket_repository;
import java.util.*;

public class Ticket_service {
    private final Ticket_repository ticket_repository;
    private final Route_repository route_repository;

    public Ticket_service(Ticket_repository ticket_repository, Route_repository route_repository) {
        this.ticket_repository = ticket_repository;
        this.route_repository = route_repository;
    }

    public Ticket buyTicket(String userId, String routeId) {
        Route route = route_repository.findByID(routeId).orElseThrow(() -> new IllegalArgumentException("Route not found"));

        double price = route.getTransport().getPricePerKm() * route.getDistance();
        Ticket t = new Ticket(UUID.randomUUID().toString(), userId, routeId, price, Ticket_status.ACTIVE);
        ticket_repository.save(t);
        return t;
    }

    public void returnTicket(String ticketId, String userId) {
        Ticket ticket = ticket_repository.findByID(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (!ticket.getUser_id().equals(userId)) {
            throw new IllegalStateException("Cannot return other user's ticket");
        }
        if (ticket.getStatus() == Ticket_status.RETURNED) return;
        ticket.setStatus(Ticket_status.RETURNED);
        ticket_repository.update(ticket);
    }

    public List<Ticket> getUserTickets(String userId) {
        return ticket_repository.findByUserID(userId);
    }
}
