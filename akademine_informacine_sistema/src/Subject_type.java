public class Subject_type {
    private int subType_id;
    private String title;

    public Subject_type(int subType_id, String title) {
        this.subType_id = subType_id;
        this.title = title;
    }
    public String getTitle() { return title; }
    public String toString() { return title; }
}
