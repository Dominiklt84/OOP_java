public class Professor extends User {
    private int professor_id;

    public Professor(int user_id, String login, String password,
                     String firstName, String lastName,
                     int professor_id) {
        super(user_id, login, password, firstName, lastName, "PROFESSOR");
        this.professor_id = professor_id;
    }

    public int getProfessorId() { return professor_id; }
    public String toString() {
        return getFirstName() + " " + getLastName();
    }
}
