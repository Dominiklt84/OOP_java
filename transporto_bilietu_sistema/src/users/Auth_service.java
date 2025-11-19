package users;

import java.util.HashMap;
import java.util.Map;

public class Auth_service {
    private final Map<String, User> users=new HashMap<>();
    private User logged_in;

    public Auth_service() {
        users.put("user", new User("user","user", Role.USER));
        users.put("admin", new User("admin","admin", Role.ADMIN));
    }

    public boolean login(String login, String password) {
        User n = users.get(login);
        if (n == null) {
            System.out.println("Vartotojas nerastas: " + login);
            return false;
        }

        if (!n.getPassword().equals(password)) {
            System.out.println("Blogas slaptažodis: " + password);
            return false;
        }

        logged_in = n;
        System.out.println("Prisijungė: " + logged_in.getLogin()
                + " kaip " + logged_in.getRole());
        return true;
    }

    public void logout(){
        logged_in=null;
    }

    public boolean orLoggedIn(){
        return logged_in !=null;
    }

    public User getLoggedIn(){
        return logged_in;
    }
}
