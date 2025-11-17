import java.util.List;

public interface Assessment_repository {
    List<Assessment> findByProfessorAndFilters(Integer professor_id, Integer group_id, Integer subject_id, Integer semType_id);
    List<Assessment> findByStudent(int student_id, Integer subject_id, Integer semType_id);
    Assessment save(Assessment a);
    void delete(int id);
}
