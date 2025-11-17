import java.util.List;

public interface Subject_type_repository {
    List<Subject_type> findAll();
    int add(String title);
    void delete(int id);
}
