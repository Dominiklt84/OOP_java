package service;
import user.User;
import repository.User_repository;
import java.util.List;

public class User_service {
    private final User_repository user_repository;

    public User_service(User_repository user_repository) {
        this.user_repository = user_repository;
    }

    public List<User> getAllUsers() {
        return user_repository.findAll();
    }
}
