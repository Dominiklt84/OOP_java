import java.sql.*;
import java.util.Optional;

public class JDBC_user_repository implements User_repository {

    public Optional<User> findByCredentials(String login, String password) {
        String sql = """
            SELECT 
                u.user_id,
                u.first_name,
                u.last_name,
                u.login,
                u.password,
                ut.title AS role,
                s.student_id   AS s_student_id,
                p.professor_id AS p_professor_id
            FROM `user` u
            JOIN `user_type` ut ON ut.user_type_id = u.user_type_id
            LEFT JOIN `student`  s ON s.user_id = u.user_id
            LEFT JOIN `professor` p ON p.user_id = u.user_id
            WHERE u.login = ? AND u.password = ?
            """;

        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                int uid = rs.getInt("user_id");
                String fn = rs.getString("first_name");
                String ln = rs.getString("last_name");
                String lg = rs.getString("login");
                String pw = rs.getString("password");
                String role = rs.getString("role");

                switch (role == null ? "" : role.toUpperCase()) {
                    case "ADMIN" -> {
                        return Optional.of(new Admin(uid, lg, pw, fn, ln));
                    }
                    case "PROFESSOR" -> {
                        Integer profId = (Integer) rs.getObject("p_professor_id");
                        return Optional.of(new Professor(uid, lg, pw, fn, ln, profId == null ? 0 : profId));
                    }
                    case "STUDENT" -> {
                        Integer studId = (Integer) rs.getObject("s_student_id");
                        if (studId == null) throw new SQLException(
                                "Rolė STUDENT, bet lentelėje `student` nėra eilutės su user_id=" + uid);
                        return Optional.of(new Student(uid, lg, pw, fn, ln, studId, null));
                    }
                    default -> throw new SQLException("Nežinoma rolė: " + role);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB klaida prisijungimo metu: " + e.getMessage(), e);
        }
    }
}
