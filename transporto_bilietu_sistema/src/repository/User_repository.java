package repository;
import user.User;
import java.util.List;
import java.util.Optional;

public interface User_repository {
    Optional<User> findByUsername(String username);
    Optional<User> findByID(String user_id);
    List<User> findAll();
    void save(User user);
}
