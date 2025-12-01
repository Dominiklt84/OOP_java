package transport;

public class Plane extends Transport {
    public Plane(double pricePerKm) {
        super("Plane", pricePerKm);
    }
    public Transport_type getType() {
        return Transport_type.PLANE;
    }
}