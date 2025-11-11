import java.time.LocalDate;

public class Assessment {
    private Integer assessment_id;
    private int student_id;
    private int subject_id;
    private Integer professor_id;
    private int grade;
    private LocalDate date;
    private String comment;
    private String semester;

    public Assessment(Integer assessment_id, int student_id, int subject_id, Integer professor_id,
                      int grade, LocalDate date, String comment, String semester) {
        this.assessment_id = assessment_id;
        this.student_id = student_id;
        this.subject_id = subject_id;
        this.professor_id = professor_id;
        this.grade = grade;
        this.date = date;
        this.comment = comment;
        this.semester = semester;
    }

    public Integer getAssessmentId() { return assessment_id; }
    public void setAssessmentId(Integer assessment_id) { this.assessment_id = assessment_id; }

    public int getStudentId() { return student_id; }
    public void setStudentId(int student_id) { this.student_id= student_id; }

    public int getSubjectId() { return subject_id; }
    public void setSubjectId(int subject_id) { this.subject_id = subject_id; }

    public Integer getProfessorId() { return professor_id; }
    public void setProfessorId(Integer professor_id) { this.professor_id = professor_id; }

    public int getGrade() { return grade; }
    public void setGrade(int grade) { this.grade = grade; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String toString() {
        return "Assessment{id=" + assessment_id +
                ", stud=" + student_id +
                ", subj=" + subject_id +
                ", grade=" + grade +
                ", date=" + date +
                (semester!=null?(", sem="+semester):"") + "}";
    }
}
