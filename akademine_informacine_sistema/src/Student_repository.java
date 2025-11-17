import java.util.List;

public interface Student_repository {
    List<Student> findAll();
    int add(String firstName, String lastName);
    void update(int student_id, String firstName, String lastName);
    void delete(int student_id);
    List<Student> findByGroup(int group_id);
    void updateCredentials(int studentId, String login, String password);
}
