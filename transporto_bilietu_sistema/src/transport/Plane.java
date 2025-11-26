package transport;

import javax.swing.*;

public class Plane extends Transport {
    public Plane(int id, String start_city, String final_city, double distance) {
        super(id, start_city, final_city, distance);
    }
    public double price(){
        return distance * 0.3;
    }
}
