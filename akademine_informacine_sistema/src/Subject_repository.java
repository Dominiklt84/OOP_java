import java.util.List;

public interface Subject_repository {
    List<Subject> findAll();
    int add(int subType_id, int credits);
    void update(int subject_id, int subType_id, int credits);
    void delete(int subject_id);
}
