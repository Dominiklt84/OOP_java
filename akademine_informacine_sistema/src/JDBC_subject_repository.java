import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLIntegrityConstraintViolationException;

public class JDBC_subject_repository implements Subject_repository {

    private Subject map(ResultSet rs) throws SQLException {
        return new Subject(
                rs.getInt("subject_id"),
                rs.getInt("sub_type_id"),
                rs.getInt("credits"),
                rs.getString("type_title")
        );
    }

    public List<Subject> findAll() {
        String sql = """
            SELECT s.subject_id, s.sub_type_id, s.credits,
                   st.title AS type_title
            FROM subject s
            JOIN subject_type st ON st.sub_type_id = s.sub_type_id
            ORDER BY s.subject_id
            """;
        List<Subject> list = new ArrayList<>();
        try (Connection c = Data_base.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException("Nepavyko gauti dalykų", e); }
        return list;
    }

    public int add(int subTypeId, int credits) {
        String getNextIdSql = "SELECT COALESCE(MAX(subject_id),0)+1 AS next_id FROM subject";
        String insertSql = "INSERT INTO subject (subject_id, sub_type_id, credits) VALUES (?, ?, ?)";

        try (Connection c = Data_base.getConnection()) {

            int nextId = 1;
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery(getNextIdSql)) {
                if (rs.next()) nextId = rs.getInt("next_id");
            }

            try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                ps.setInt(1, nextId);
                ps.setInt(2, subTypeId);
                ps.setInt(3, credits);
                ps.executeUpdate();
            }

            return nextId;

        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko pridėti dalyko: " + e.getMessage(), e);
        }
    }

    public void update(int subjectId, int subTypeId, int credits) {
        String sql = "UPDATE subject SET sub_type_id=?, credits=? WHERE subject_id=?";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, subTypeId);
            ps.setInt(2, credits);
            ps.setInt(3, subjectId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Nepavyko atnaujinti dalyko", e); }
    }

    public void delete(int subjectId) {
        String sql = "DELETE FROM subject WHERE subject_id=?";

        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException(
                    "Negalima ištrinti dalyko.\n" +
                            "Pirma:\n" +
                            " • nuimkite šį dalyką nuo visų grupių (Assignments skiltyje),\n" +
                            " • ištrinkite pažymius, susijusius su šiuo dalyku,\n" +
                            " • įsitikinkite, kad nėra kitų ryšių su šiuo dalyku.\n\n" +
                            "Tada bandykite trinti dar kartą.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko ištrinti dalyko: " + e.getMessage(), e);
        }
    }

}
