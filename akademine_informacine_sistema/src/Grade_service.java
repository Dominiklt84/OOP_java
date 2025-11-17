import java.util.List;

public class Grade_service {

    private final Assessment_repository repo;

    public Grade_service() {
        this.repo = new JDBC_assessment_repository();
    }

    public List<Assessment> listForStudent(int studentId,
                                           Integer subjectId,
                                           Integer semTypeId) {
        return repo.findByStudent(studentId, subjectId, semTypeId);
    }

    public void delete(int assessmentId) {
        repo.delete(assessmentId);
    }
}
