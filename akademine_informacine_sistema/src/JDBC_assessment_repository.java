import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<Assessment> findByProfessorAndFilters(Integer professor_id, Integer group_id, Integer subject_id, Integer semType_id) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.assessment_id, a.student_id, a.subject_id, a.professor_id, a.sem_type_id, a.worth, a.comment, a.introduced " +
                        "FROM assessment a ");
        List<Object> params = new ArrayList<>();
        if (group_id != null) { sql.append("JOIN `group` g ON g.student_id=a.student_id AND g.group_id=? "); params.add(group_id); }
        sql.append("WHERE 1=1 ");
        if (professor_id != null) { sql.append("AND a.professor_id=? "); params.add(professor_id); }
        if (subject_id   != null) { sql.append("AND a.subject_id=? ");   params.add(subject_id); }
        if (semType_id  != null) { sql.append("AND a.sem_type_id=? ");  params.add(semType_id); }
        sql.append("ORDER BY a.introduced DESC, a.assessment_id DESC");

        List<Assessment> list = new ArrayList<>();
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i=0;i<params.size();i++) ps.setObject(i+1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Nepavyko gauti pažymių", e); }
        return list;
    }

    public List<Assessment> findByStudent(int studentId, Integer subjectId, Integer semTypeId) {
        StringBuilder sql = new StringBuilder(
                "SELECT assessment_id, student_id, subject_id, professor_id, sem_type_id, worth, comment, introduced " +
                        "FROM assessment WHERE student_id=? ");
        List<Object> params = new ArrayList<>();
        params.add(studentId);
        if (subjectId != null) { sql.append("AND subject_id=? "); params.add(subjectId); }
        if (semTypeId != null) { sql.append("AND sem_type_id=? "); params.add(semTypeId); }
        sql.append("ORDER BY introduced DESC, assessment_id DESC");

        List<Assessment> list = new ArrayList<>();
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i=0;i<params.size();i++) ps.setObject(i+1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Nepavyko gauti studento pažymių", e); }
        return list;
    }

    public Optional<Assessment> findById(int id) {
        String sql = "SELECT assessment_id, student_id, subject_id, professor_id, sem_type_id, worth, comment, introduced " +
                "FROM assessment WHERE assessment_id=?";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Nepavyko rasti pažymio", e); }
        return Optional.empty();
    }

    public Assessment save(Assessment a) {
        try (Connection c = Data_base.getConnection()) {
            if (a.getAssessmentId() == null) {
                // INSERT (naudojam NOW() introduced laukui, jei nepaduodamas)
                String sql = "INSERT INTO assessment (student_id, subject_id, professor_id, sem_type_id, worth, comment, introduced) " +
                        "VALUES (?, ?, ?, ?, ?, ?, NOW())";
                try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, a.getStudentId());
                    ps.setInt(2, a.getSubjectId());
                    if (a.getProfessorId()==null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, a.getProfessorId());
                    if (a.getSemTypeId()==null)   ps.setNull(4, Types.INTEGER); else ps.setInt(4, a.getSemTypeId());
                    ps.setDouble(5, a.getWorth());
                    ps.setString(6, a.getComment());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) a.setAssessmentId(keys.getInt(1));
                    }
                }
            } else {
                String sql = "UPDATE assessment SET student_id=?, subject_id=?, professor_id=?, sem_type_id=?, worth=?, comment=? " +
                        "WHERE assessment_id=?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, a.getStudentId());
                    ps.setInt(2, a.getSubjectId());
                    if (a.getProfessorId()==null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, a.getProfessorId());
                    if (a.getSemTypeId()==null)   ps.setNull(4, Types.INTEGER); else ps.setInt(4, a.getSemTypeId());
                    ps.setDouble(5, a.getWorth());
                    ps.setString(6, a.getComment());
                    ps.setInt(7, a.getAssessmentId());
                    ps.executeUpdate();
                }
            }
            return a;
        } catch (SQLException e) { throw new RuntimeException("Nepavyko išsaugoti pažymio", e); }
    }

    public void delete(int id) {
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM assessment WHERE assessment_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Nepavyko ištrinti pažymio", e); }
    }
}
