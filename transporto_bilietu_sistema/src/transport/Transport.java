package transport;

public abstract class Transport implements Transport_methods {
    protected int id;
    protected String start_city;
    protected String final_city;
    protected double distance;

    public Transport(int id, String start_city, String final_city, double distance) {
        this.id = id;
        this.start_city = start_city;
        this.final_city = final_city;
        this.distance = distance;
    }

    public abstract double price();
}
