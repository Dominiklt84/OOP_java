package transport;

public class Train extends Transport {
    public Train(String start_city, String final_city, double distance) {
        super(start_city, final_city, distance);
    }
    public double price() {
        return distance * 0.2;
    }
}
