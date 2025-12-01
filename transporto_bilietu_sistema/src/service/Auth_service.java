package service;
import user.*;
import repository.User_repository;
import java.util.Optional;
import java.util.UUID;

public class Auth_service {
    private final User_repository user_repository;

    public Auth_service(User_repository user_repository) {
        this.user_repository = user_repository;

    }

    public Optional<User> login(String username, String password) {
        return user_repository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .map(u -> {
                    if (u.getRole() == Role.ADMIN) {
                        return new Admin(u.getUserID(), u.getUsername(), u.getPassword(), u.getRole());
                    } else {
                        return new Client(u.getUserID(), u.getUsername(), u.getPassword(), u.getRole());
                    }
                });
    }

    public User registerClient(String username, String password) {
        if (user_repository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        String user_id = UUID.randomUUID().toString();
        User user = new Client(user_id, username, password, Role.CLIENT);

        user_repository.save(user);
        return user;
    }

    public User createAdminIfNotExists(String username, String password) {
        Optional<User> existing = user_repository.findByUsername(username);
        if (existing.isPresent()) {
            User u = existing.get();
            return new Admin(u.getUserID(), u.getUsername(), u.getPassword(), u.getRole());
        }
        String admin_id = UUID.randomUUID().toString();
        User admin = new Admin(admin_id, username, password, Role.ADMIN);

        user_repository.save(admin);
        return admin;
    }
}
