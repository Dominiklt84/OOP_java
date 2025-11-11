public class User {
    private int user_id;
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private String role;

    public User(int user_id, String firstName, String lastName, String login, String password, String role) {
        this.user_id = user_id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.role = role;
    }
    public int getId() {return user_id;}
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }

    public String displayInfo() {
        return firstName + " " + lastName + " (" + role + ")";
    }
}
