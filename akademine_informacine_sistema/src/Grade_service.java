import java.util.List;

public class Grade_service {

    private final Assessment_repository repo;

    public Grade_service() {
        this.repo = new JDBC_assessment_repository();
    }

    public List<Assessment> listForProfessor(Integer professorId,
                                             Integer groupId,
                                             Integer subjectId,
                                             Integer semTypeId) {
        return repo.findByProfessorAndFilters(professorId, groupId, subjectId, semTypeId);
    }

    public List<Assessment> listForStudent(int studentId,
                                           Integer subjectId,
                                           Integer semTypeId) {
        return repo.findByStudent(studentId, subjectId, semTypeId);
    }

    public Assessment save(Assessment a) {
        double w = a.getWorth();
        if (Double.isNaN(w) || w < 0.0 || w > 10.0) {
            throw new IllegalArgumentException("Pažymio reikšmė (worth) turi būti tarp 0 ir 10.");
        }
        return repo.save(a);
    }

    public void delete(int assessmentId) {
        repo.delete(assessmentId);
    }
}
