import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBC_assessment_repository implements Assessment_repository {

    private Assessment map(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("introduced");
        return new Assessment(
                (Integer) rs.getObject("assessment_id"),
                rs.getInt("student_id"),
                rs.getInt("subject_id"),
                (Integer) rs.getObject("professor_id"),
                rs.getDouble("worth"),
                (Integer) rs.getObject("sem_type_id"),
                rs.getString("comment"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }

    public List<Assessment> findByProfessorAndFilters(Integer professor_id, Integer group_id,
                                                      Integer subject_id, Integer semType_id) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.assessment_id, a.student_id, a.subject_id, a.professor_id, " +
                        "a.sem_type_id, a.worth, a.comment, a.introduced " +
                        "FROM assessment a ");
        List<Object> params = new ArrayList<>();
        if (group_id != null) {
            sql.append("JOIN group_student gs ON gs.student_id = a.student_id AND gs.group_id = ? ");
            params.add(group_id);
        }
        sql.append("WHERE 1=1 ");
        if (professor_id != null) { sql.append("AND a.professor_id=? "); params.add(professor_id); }
        if (subject_id   != null) { sql.append("AND a.subject_id=? ");   params.add(subject_id); }
        if (semType_id   != null) { sql.append("AND a.sem_type_id=? ");  params.add(semType_id); }
        sql.append("ORDER BY a.introduced DESC, a.assessment_id DESC");

        List<Assessment> list = new ArrayList<>();
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko gauti pažymių: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Assessment> findByStudent(int studentId, Integer subjectId, Integer semTypeId) {
        StringBuilder sql = new StringBuilder(
                "SELECT assessment_id, student_id, subject_id, professor_id, " +
                        "sem_type_id, worth, comment, introduced " +
                        "FROM assessment WHERE student_id=? ");
        List<Object> params = new ArrayList<>();
        params.add(studentId);
        if (subjectId != null) { sql.append("AND subject_id=? "); params.add(subjectId); }
        if (semTypeId != null) { sql.append("AND sem_type_id=? "); params.add(semTypeId); }
        sql.append("ORDER BY introduced DESC, assessment_id DESC");

        List<Assessment> list = new ArrayList<>();
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko gauti studento pažymių: " + e.getMessage(), e);
        }
        return list;
    }

    public Assessment save(Assessment a) {
        try (Connection c = Data_base.getConnection()) {
            if (a.getAssessmentId() == null) {
                int nextId;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT COALESCE(MAX(assessment_id),0)+1 FROM assessment");
                     ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    nextId = rs.getInt(1);
                }

                String sql = """
                    INSERT INTO assessment (
                        assessment_id,
                        student_id,
                        subject_id,
                        professor_id,
                        sem_type_id,
                        worth,
                        comment,
                        introduced
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())
                    """;

                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, nextId);
                    ps.setInt(2, a.getStudentId());
                    ps.setInt(3, a.getSubjectId());
                    if (a.getProfessorId() == null)
                        ps.setNull(4, Types.INTEGER);
                    else
                        ps.setInt(4, a.getProfessorId());
                    if (a.getSemTypeId() == null)
                        ps.setNull(5, Types.INTEGER);
                    else
                        ps.setInt(5, a.getSemTypeId());
                    ps.setDouble(6, a.getWorth());
                    ps.setString(7, a.getComment());
                    ps.executeUpdate();
                    a.setAssessmentId(nextId);
                }
            } else {
                String sql = """
                    UPDATE assessment
                    SET student_id=?,
                        subject_id=?,
                        professor_id=?,
                        sem_type_id=?,
                        worth=?,
                        comment=?
                    WHERE assessment_id=?
                    """;
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, a.getStudentId());
                    ps.setInt(2, a.getSubjectId());
                    if (a.getProfessorId() == null)
                        ps.setNull(3, Types.INTEGER);
                    else
                        ps.setInt(3, a.getProfessorId());
                    if (a.getSemTypeId() == null)
                        ps.setNull(4, Types.INTEGER);
                    else
                        ps.setInt(4, a.getSemTypeId());
                    ps.setDouble(5, a.getWorth());
                    ps.setString(6, a.getComment());
                    ps.setInt(7, a.getAssessmentId());
                    ps.executeUpdate();
                }
            }
            return a;
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko išsaugoti pažymio: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM assessment WHERE assessment_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko ištrinti pažymio: " + e.getMessage(), e);
        }
    }
}
