package transport;

public abstract class Transport implements Transport_methods {
    protected String start_city;
    protected String final_city;
    protected double distance;

    public Transport(String start_city, String final_city, double distance) {
        this.start_city = start_city;
        this.final_city = final_city;
        this.distance = distance;
    }

    public abstract double price();
}
