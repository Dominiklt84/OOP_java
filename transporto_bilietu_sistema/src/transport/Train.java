package transport;

import javax.swing.*;

public class Train extends Transport {
    public Train(int id,String start_city, String final_city, double distance) {
        super(id,start_city, final_city, distance);
    }
    public double price() {
        return distance * 0.2;
    }
}
