import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JDBC_assessment_repository implements Assessment_repository {

    private Integer nextIdIfNeeded(Connection c) {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(assessment_id),0)+1 AS next_id FROM assessment")) {
            if (rs.next()) return rs.getInt("next_id");
        } catch (SQLException e) {
        }
        return null;
    }

    public List<Assessment> findByFilters(Integer professorId, Integer groupId, Integer subjectId,
                                          LocalDate fromDate, LocalDate toDate, String semester) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.assessment_id, a.student_id, a.subject_id, a.professor_id, a.grade, a.date, a.comment, a.semester " +
                        "FROM assessment a ");
        if (groupId != null) {
            sql.append("JOIN `group` g ON g.student_id = a.student_id AND g.group_id = ? ");
        }
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (groupId != null) params.add(groupId);
        if (professorId != null) { sql.append("AND a.professor_id = ? "); params.add(professorId); }
        if (subjectId   != null) { sql.append("AND a.subject_id   = ? "); params.add(subjectId); }
        if (fromDate    != null) { sql.append("AND a.date >= ? ");      params.add(Date.valueOf(fromDate)); }
        if (toDate      != null) { sql.append("AND a.date <= ? ");      params.add(Date.valueOf(toDate)); }
        if (semester    != null && !semester.isBlank()) { sql.append("AND a.semester = ? "); params.add(semester); }

        sql.append("ORDER BY a.date DESC, a.assessment_id DESC");

        List<Assessment> list = new ArrayList<>();
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i+1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko gauti pažymių", e);
        }
        return list;
    }

    public List<Assessment> findByStudent(int studentId, Integer subjectId, String semester) {
        StringBuilder sql = new StringBuilder(
                "SELECT assessment_id, student_id, subject_id, professor_id, grade, date, comment, semester " +
                        "FROM assessment WHERE student_id=? ");
        List<Object> params = new ArrayList<>();
        params.add(studentId);

        if (subjectId != null) { sql.append("AND subject_id=? "); params.add(subjectId); }
        if (semester  != null && !semester.isBlank()) { sql.append("AND semester=? "); params.add(semester); }
        sql.append("ORDER BY date DESC, assessment_id DESC");

        List<Assessment> list = new ArrayList<>();
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i+1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko gauti studento pažymių", e);
        }
        return list;
    }

    public Optional<Assessment> findById(int assessmentId) {
        String sql = "SELECT assessment_id, student_id, subject_id, professor_id, grade, date, comment, semester " +
                "FROM assessment WHERE assessment_id=?";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko rasti pažymio", e);
        }
        return Optional.empty();
    }

    public Assessment save(Assessment a) {
        try (Connection c = Data_base.getConnection()) {
            if (a.getAssessmentId() == null) {
                Integer next = nextIdIfNeeded(c); // jei auto-inc, gali likti null
                String sql;
                if (next == null) {
                    sql = "INSERT INTO assessment (student_id, subject_id, professor_id, grade, date, comment, semester) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                } else {
                    sql = "INSERT INTO assessment (assessment_id, student_id, subject_id, professor_id, grade, date, comment, semester) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                }
                try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    int idx = 1;
                    if (next != null) { ps.setInt(idx++, next); }
                    ps.setInt(idx++, a.getStudentId());
                    ps.setInt(idx++, a.getSubjectId());
                    if (a.getProfessorId() == null) ps.setNull(idx++, Types.INTEGER); else ps.setInt(idx++, a.getProfessorId());
                    ps.setInt(idx++, a.getGrade());
                    ps.setDate(idx++, Date.valueOf(a.getDate()!=null? a.getDate() : LocalDate.now()));
                    ps.setString(idx++, a.getComment());
                    ps.setString(idx++, a.getSemester());
                    ps.executeUpdate();

                    if (next != null) {
                        a.setAssessmentId(next);
                    } else {
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) a.setAssessmentId(keys.getInt(1));
                        }
                    }
                }
            } else {
                String sql = "UPDATE assessment SET student_id=?, subject_id=?, professor_id=?, grade=?, date=?, comment=?, semester=? " +
                        "WHERE assessment_id=?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, a.getStudentId());
                    ps.setInt(2, a.getSubjectId());
                    if (a.getProfessorId() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, a.getProfessorId());
                    ps.setInt(4, a.getGrade());
                    ps.setDate(5, Date.valueOf(a.getDate()!=null? a.getDate() : LocalDate.now()));
                    ps.setString(6, a.getComment());
                    ps.setString(7, a.getSemester());
                    ps.setInt(8, a.getAssessmentId());
                    ps.executeUpdate();
                }
            }
            return a;
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko išsaugoti pažymio", e);
        }
    }

    public void delete(int assessmentId) {
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM assessment WHERE assessment_id=?")) {
            ps.setInt(1, assessmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko ištrinti pažymio", e);
        }
    }

    private Assessment map(ResultSet rs) throws SQLException {
        return new Assessment(
                (Integer) rs.getObject("assessment_id"),
                rs.getInt("student_id"),
                rs.getInt("subject_id"),
                (Integer) rs.getObject("professor_id"),
                rs.getInt("grade"),
                rs.getDate("date").toLocalDate(),
                rs.getString("comment"),
                rs.getString("semester")
        );
    }
}
