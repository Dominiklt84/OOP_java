package users;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Auth_service {
    private final Map<String, User> users=new HashMap<>();
    private User logged_in;
    private final File userFile = new File("data/users.txt");

    public Auth_service() {
        importFromFile();
        if (!users.containsKey("admin")) {
            User admin = new User("admin", "admin", Role.ADMIN);
            users.put("admin", admin);
            saveOneLine(admin);
        }
    }

    public boolean registration(String login, String password) {
        if (users.containsKey(login)) return false;
        User u = new User(login, password, Role.USER);
        users.put(login, u);
        saveOneLine(u);
        return true;
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

    private void importFromFile(){
        if (!userFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";");
                if (parts.length != 3) continue;
                String login = parts[0];
                String password = parts[1];
                Role role = Role.valueOf(parts[2]);
                users.put(login, new User(login, password, role));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveOneLine(User u){
        try {
            userFile.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(userFile, true)) {
                fw.write(u.getLogin() + ";" +
                        u.getPassword() + ";" +
                        u.getRole().name() + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
