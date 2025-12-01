package user;

public class User {
    private final String user_id;
    private String username;
    private String password;
    private Role role;

    public User(String id, String username, String password, Role role) {
        this.user_id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUserID() { return user_id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    public String toString() {
        return username + " (" + role + ")";
    }
}
