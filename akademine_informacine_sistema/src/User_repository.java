import java.sql.*;
import java.util.Optional;

public class User_repository {
    public Optional<User> findByCredentials(String login, String password) {
        String sql = """
            
                SELECT u.user_id, u.first_name, u.last_name, u.login, u.password, ut.title AS role
                            FROM user u
              JOIN user_type ut ON ut.user_type_id = u.user_type_id
                            WHERE u.login = ? AND u.
                password = ?
            """;

        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int user_id = rs.getInt("user_id");
                String fn = rs.getString("first_name");
                String ln = rs.getString("last_name");
                String role = rs.getString("role");

                switch (role.toUpperCase()) {
                    case "ADMIN":
                        return Optional.of(new Admin(user_id, login, password, fn, ln));
                    case "PROFESSOR":
                        int professor_id = rs.getInt("professor_id");
                        return Optional.of(new Professor(user_id, login, password, fn, ln, professor_id));
                    case "STUDENT":
                        int student_id = rs.getInt("student_id");
                        Integer group_id = (Integer) rs.getObject("student_group_id");
                        return Optional.of(new Student(user_id, login, password, fn, ln, student_id, group_id));
                    default:
                        return Optional.empty();
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Data base klaida", e);
        }
    }
}