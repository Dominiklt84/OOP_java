public class Ticket {
    private final Transport transport;
    private final double price_ticket;

    public Ticket(Transport transport){
        this.transport = transport;
        this.price_ticket = transport.price();
    }
    public void print_ticket(){
        transport.print_info();
        System.out.println("Ticket price: " + price_ticket + " EUR");
    }
    public double getPrice_ticket(){
        return price_ticket;
    }
}
