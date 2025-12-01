package transport;

public abstract class Transport {
    private final String name;
    private double pricePerKm;

    protected Transport(String name,double pricePerKm) {
        this.name = name;
        this.pricePerKm = pricePerKm;
    }
    public String getName() {
        return name;
    }
    public double getPricePerKm() {
        return pricePerKm;
    }
    public abstract Transport_type getType();

    public String toString() {
        return name + " (" + pricePerKm + " €/km)";
    }
}
