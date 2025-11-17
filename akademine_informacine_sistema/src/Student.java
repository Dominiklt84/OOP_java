public class Student extends User {
    private int student_id;
    private Integer group_id;

    public Student(int user_id, String login, String password,
                   String firstName, String lastName,
                   int student_id, Integer group_id) {
        super(user_id, login, password, firstName, lastName, "STUDENT");
        this.student_id = student_id;
        this.group_id = group_id;
    }

    public int getStudentId() { return student_id; }
    public String toString() {
        return getFirstName() + " " + getLastName();
    }
}
