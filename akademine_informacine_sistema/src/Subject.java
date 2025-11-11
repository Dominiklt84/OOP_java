public class Subject {
    private int subject_id;
    private int subType_id;
    private int credits;
    private String subTypeTitle;

    public Subject(int subject_id, int subType_id, int credits, String subTypeTitle) {
        this.subject_id = subject_id;
        this.subType_id = subType_id;
        this.credits = credits;
        this.subTypeTitle = subTypeTitle;
    }

    public int getSubjectId() { return subject_id; }
    public int getSubTypeId() { return subType_id; }
    public void setSubTypeId(int subTypeId) { this.subType_id = subTypeId; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public String getSubTypeTitle() { return subTypeTitle; }
    public void setSubTypeTitle(String t) { this.subTypeTitle = t; }

    @Override public String toString() { return subTypeTitle + " (" + credits + " kr.)"; }
}
