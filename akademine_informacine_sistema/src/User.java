public class User {
    protected int user_id;
    protected String login;
    protected String password;
    protected String firstName;
    protected String lastName;
    protected String role;

    public User(int user_id, String login, String password,
                String firstName, String lastName, String role) {
        this.user_id = user_id;
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public int getUserId()     { return user_id; }
    public String getLogin()   { return login; }
    public String getPassword(){ return password; }
    public String getFirstName(){ return firstName; }
    public String getLastName(){ return lastName; }
    public String getRole()    { return role; }

    public void setLogin(String login)       { this.login = login; }
    public void setPassword(String password) { this.password = password; }
    public void setFirstName(String firstName){ this.firstName = firstName; }
    public void setLastName(String lastName){ this.lastName = lastName; }
    public String displayInfo() {
        return firstName + " " + lastName + " (" + role + ")";
    }
}
