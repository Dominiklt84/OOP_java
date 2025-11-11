import java.time.LocalDate;
import java.util.List;

public class Grade_service {
    private final Assessment_repository repo;

    public Grade_service() { this.repo = new JDBC_assessment_repository(); }

    public List<Assessment> listForProfessor(Integer professorId, Integer groupId, Integer subjectId,
                                             LocalDate from, LocalDate to, String semester) {
        return repo.findByFilters(professorId, groupId, subjectId, from, to, semester);
    }

    public List<Assessment> listForStudent(int studentId, Integer subjectId, String semester) {
        return repo.findByStudent(studentId, subjectId, semester);
    }

    public Assessment save(Assessment a) {
        if (a.getGrade() < 1 || a.getGrade() > 10) {
            throw new IllegalArgumentException("Pažymys turi būti tarp 1 ir 10");
        }
        return repo.save(a);
    }

    public void delete(int assessmentId) { repo.delete(assessmentId); }
}
