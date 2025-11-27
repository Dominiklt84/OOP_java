package transport;

public abstract class Transport {
    private final Transport_type type;

    protected Transport(Transport_type type) {
        this.type = type;
    }

    public Transport_type getType() {
        return type;
    }

    public String toString() {
        return type.name();
    }
}
