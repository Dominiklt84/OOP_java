public class Student extends User {
    public Student(int id, String login, String password, String firstName, String lastName) {
        super(id, login, password, firstName, lastName, "STUDENT");
    }
}
