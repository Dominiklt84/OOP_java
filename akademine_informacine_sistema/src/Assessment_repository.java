import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface Assessment_repository {
    List<Assessment> findByFilters(Integer professorId, Integer groupId, Integer subjectId,
                                   LocalDate fromDate, LocalDate toDate, String semester);
    List<Assessment> findByStudent(int studentId, Integer subjectId, String semester);
    Optional<Assessment> findById(int assessmentId);
    Assessment save(Assessment a);

    void delete(int assessmentId);
}
