package transport;

public class Bus extends Transport {
    public Bus(double pricePerKm) {
        super("Bus", pricePerKm);
    }
    public Transport_type getType() {
        return Transport_type.BUS;
    }
}