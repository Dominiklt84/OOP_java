import java.util.List;
import java.util.Optional;

public interface Group_repository {
    List<Group> findAll();
    Optional<Group> findById(int id);
    void add(Group group);
    void update(Group group);
    void delete(int id);
}
