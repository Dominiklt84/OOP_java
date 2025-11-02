import java.util.HashMap;

public class Auth_Service {
    private final HashMap<String, String> users= new HashMap<>();
    private User currentUser;

    public boolean registration(String username, String password){
        if(users.containsKey(username)){
            return false;
        }else{
            users.put(username, password);
            return true;
        }
    }

    public boolean login(String username, String password){
        if(users.containsKey(username) &&users.get(username).equals(password)){
            currentUser = new User(username);
            return true;
        }
        return false;
    }

    public void logout(){
        currentUser = null;
    }

    public boolean orLogin(){
        return currentUser != null;
    }

    public String nowLogged(){
        return currentUser != null ? currentUser.getNickname() : "Nothing";
    }
}
