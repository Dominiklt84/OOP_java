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
}
