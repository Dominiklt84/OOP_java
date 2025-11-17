import java.util.Optional;

public interface User_repository {
    Optional<User> findByCredentials(String login, String password);
}
