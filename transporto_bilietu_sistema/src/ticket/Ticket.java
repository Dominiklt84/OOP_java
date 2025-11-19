package ticket;

import transport.Transport;

public class Ticket {
    private final Transport transport;
    private final double price_ticket;

    public Ticket(Transport transport){
        this.transport = transport;
        this.price_ticket = transport.price();
    }
    public Transport getTransport(){
        return transport;
   }
    public double getPrice_ticket(){
        return price_ticket;
    }
}
