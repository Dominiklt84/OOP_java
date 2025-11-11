import java.util.List;
import java.util.Optional;

public interface Subject_repository {
    List<Subject> findAll();
    Optional<Subject> findById(int id);
    int add(int subType_id, int credits);
    void update(int subject_id, int subType_id, int credits);
    void delete(int subject_id);
}
