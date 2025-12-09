package file;
import user.*;
import repository.User_repository;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class File_user implements User_repository {
    private final Path path;

    public File_user(String filePath) {
        this.path = Path.of(filePath);
    }

    public Optional<User> findByUsername(String username) {
        return readAll().stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }

    public List<User> findAll() {
        return readAll();
    }

    public void save(User user) {
        List<User> users = readAll();
        users.add(user);
        writeAll(users);
    }

    private List<User> readAll() {
        List<User> users = new ArrayList<>();
        if (!Files.exists(path)) return users;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 4) continue;
                String user_id = parts[0];
                String username = parts[1];
                String password = parts[2];
                Role role = Role.valueOf(parts[3]);

                User u;
                if (role == Role.ADMIN) {
                    u = new Admin(user_id, username, password, role);
                } else {
                    u = new Client(user_id, username, password, role);
                }

                users.add(u);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    private void writeAll(List<User> users) {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            for (User u : users) {
                bw.write(u.getUserID() + ";" + u.getUsername() + ";" +
                        u.getPassword() + ";" + u.getRole().name());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
