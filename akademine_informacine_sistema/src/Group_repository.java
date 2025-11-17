import java.util.List;

public interface Group_repository {
    List<Group> findAll();
    void add(Group group);
    void update(Group group);
    void delete(int id);
}
