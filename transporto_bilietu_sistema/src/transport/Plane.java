package transport;

public class Plane extends Transport {
    public Plane(String start_city, String final_city, double distance) {
        super(start_city, final_city, distance);
    }
    public double price(){
        return distance * 0.3;
    }
}
