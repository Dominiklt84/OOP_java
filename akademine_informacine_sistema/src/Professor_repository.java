import java.util.List;
import java.util.Optional;

public interface Professor_repository {
    List<Professor> findAll();
    Optional<Professor> findByProfessorId(int professor_id);
    int add(String firstName, String lastName);
    void update(int professor_id, String firstName, String lastName);
    void delete(int professor_id);
    void updateCredentials(int professorId, String login, String password);
}
