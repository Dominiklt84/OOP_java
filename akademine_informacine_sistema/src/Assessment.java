import java.time.LocalDateTime;

public class Assessment {
    private Integer assessment_id;
    private int student_id;
    private int subject_id;
    private Integer professor_id;     // gali būti null
    private double worth;            // vietoj grade
    private Integer semType_id;       // vietoj string "semester"
    private String comment;
    private LocalDateTime introduced; // TIMESTAMP

    public Assessment(Integer assessment_id, int student_id, int subject_id,
                      Integer professor_id, double worth, Integer semType_id,
                      String comment, LocalDateTime introduced) {
        this.assessment_id = assessment_id;
        this.student_id = student_id;
        this.subject_id = subject_id;
        this.professor_id = professor_id;
        this.worth = worth;
        this.semType_id = semType_id;
        this.comment = comment;
        this.introduced = introduced;
    }

    public Integer getAssessmentId() { return assessment_id; }
    public void setAssessmentId(Integer assessment_id) { this.assessment_id = assessment_id; }
    public int getStudentId() { return student_id; }
    public int getSubjectId() { return subject_id; }
    public Integer getProfessorId() { return professor_id; }
    public double getWorth() { return worth; }
    public Integer getSemTypeId() { return semType_id; }
    public String getComment() { return comment; }
    public LocalDateTime getIntroduced() { return introduced; }
}
