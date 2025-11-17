import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBC_subject_type_repository implements Subject_type_repository {
    public List<Subject_type> findAll() {
        String sql = "SELECT sub_type_id, title FROM subject_type ORDER BY title";
        List<Subject_type> list = new ArrayList<>();
        try (Connection c = Data_base.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Subject_type(rs.getInt("sub_type_id"), rs.getString("title")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko nuskaityti subject_type", e);
        }
        return list;
    }

    public int add(String title) {
        String getNextIdSql = "SELECT COALESCE(MAX(sub_type_id),0)+1 AS next_id FROM subject_type";
        String insertSql    = "INSERT INTO subject_type (sub_type_id, title) VALUES (?, ?)";

        try (Connection c = Data_base.getConnection()) {
            int nextId = 1;
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery(getNextIdSql)) {
                if (rs.next()) nextId = rs.getInt("next_id");
            }

            try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                ps.setInt(1, nextId);
                ps.setString(2, title);
                ps.executeUpdate();
            }

            return nextId;
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko sukurti dalyko tipo: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM subject_type WHERE sub_type_id=?";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko ištrinti dalyko tipo: " + e.getMessage(), e);
        }
    }
}
