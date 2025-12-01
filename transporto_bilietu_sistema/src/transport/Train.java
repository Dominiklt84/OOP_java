package transport;

public class Train extends Transport {
    public Train(double pricePerKm) {
        super("Train", pricePerKm);
    }
    public Transport_type getType() {
        return Transport_type.TRAIN;
    }
}