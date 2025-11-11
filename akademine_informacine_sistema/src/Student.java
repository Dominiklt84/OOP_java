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

    public int getStudentId() {
        return student_id;
    }

    public void setStudentId(int studentId) {
        this.student_id = student_id;
    }

    public Integer getGroupId() {
        return group_id;
    }

    public void setGroupId(Integer groupId) {
        this.group_id = group_id;
    }

    public String toString() {
        return getFirstName() + " " + getLastName();
    }
}
