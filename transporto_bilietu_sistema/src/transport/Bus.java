package transport;

import javax.swing.*;

public class Bus extends Transport {
    public Bus(int id, String start_city, String final_city, double distance){
        super(id,start_city,final_city,distance);
    }
    public double price(){
        return distance * 0.1;
    }
}
