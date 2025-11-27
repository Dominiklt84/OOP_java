package ticket;

public class Ticket {
    private final String ticket_id;
    private final String user_id;
    private final String route_id;
    private final double price;
    private Ticket_status status;

    public Ticket(String ticket_id, String user_id, String route_id, double price, Ticket_status status) {
        this.ticket_id = ticket_id;
        this.user_id = user_id;
        this.route_id = route_id;
        this.price = price;
        this.status = status;
    }

    public String getTicket_id() {return ticket_id;}
    public String getUser_id() {return user_id;}
    public String getRoute_id() {return route_id;}
    public double getPrice() {return price;}
    public Ticket_status getStatus() {return status;}
    public void setStatus(Ticket_status status) {this.status = status;}
    public String toString() {
        return "Ticket " + ticket_id.substring(0, 6) + " | " + price + "€ | " + status;
    }
}
