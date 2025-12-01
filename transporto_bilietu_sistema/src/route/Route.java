package route;
import transport.Transport;

public class Route {
    private final String route_id;
    private String from;
    private String to;
    private double distance;
    private Transport transport;

    public Route(String route_id, String from, String to, double distance, Transport transport) {
        this.route_id = route_id;
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.transport = transport;
    }
    public String getRoute_id() {
        return route_id;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public double getDistance() {
        return distance;
    }
    public Transport getTransport() {
        return transport;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public void setTransport(Transport transport) {
        this.transport = transport;
    }
    public String toString() {
        double price = transport.getPricePerKm() * distance;
        return from + " → " + to +
                " | " +  String.format("%.2f €", price) +
                " | " + transport.getName();
    }
}
