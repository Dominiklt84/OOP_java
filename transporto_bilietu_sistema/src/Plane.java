public class Plane extends Transport{
    public Plane(String start_city, String final_city, double distnace) {
        super(start_city, final_city, distnace);
    }
    public double price(){
        return distance * 0.3;
    }
}
