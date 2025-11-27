package route;
import transport.Transport_type;

public class Route {
    private final String route_id;
    private String from;
    private String to;
    private double distance;
    private Transport_type transport_type;

    public Route(String route_id, String from, String to, double distanceKm, Transport_type transport_type) {
        this.route_id = route_id;
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.transport_type = transport_type;
    }

    public String getRoute_id() {return route_id;}
    public String getFrom() {return from;}
    public String getTo() {return to;}
    public double getDistance() {return distance;}
    public Transport_type getTransport_type() {return transport_type;}
    public void setFrom(String from) {this.from = from;}
    public void setTo(String to) {this.to = to;}
    public void setDistance(double distance) {this.distance = distance;}
    public void setTransport_type(Transport_type transport_type) {this.transport_type = transport_type;}

    public String toString() {
        return from + " -> " + to + " (" + transport_type + ", " + distance + " km)";
    }
}
