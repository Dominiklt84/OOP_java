package users;

public class User {
    private final String firstname;
    private final String lastname;
    private final Role role;

    public User(String firstname, String lastname, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }
    public String getFirstname() {
        return firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public Role getRole() {
        return role;
    }
}
