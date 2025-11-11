import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JDBC_student_repository implements Student_repository {

    public List<Student> findAll() {
        String sql = """
            SELECT s.student_id,
                   u.user_id, u.first_name, u.last_name, u.login, u.password
            FROM student s
            JOIN `user` u ON u.user_id = s.user_id
            ORDER BY s.student_id
            """;
        List<Student> list = new ArrayList<>();
        try (Connection c = Data_base.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("user_id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("student_id"),
                        null
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko nuskaityti studentų", e);
        }
        return list;
    }

    public Optional<Student> findById(int studentId) {
        String sql = """
            SELECT s.student_id,
                   u.user_id, u.first_name, u.last_name, u.login, u.password
            FROM student s
            JOIN `user` u ON u.user_id = s.user_id
            WHERE s.student_id = ?
            """;
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Student(
                            rs.getInt("user_id"),
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getInt("student_id"),
                            null
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko rasti studento", e);
        }
        return Optional.empty();
    }

    private int getStudentTypeId(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT user_type_id FROM `user_type` WHERE title='STUDENT'")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("Lentelėje user_type nėra įrašo 'STUDENT'");
            }
        }
    }

    public int add(String firstName, String lastName) {
        String login = firstName;
        String password = lastName;

        String insertUser = "INSERT INTO `user` (user_type_id, first_name, last_name, login, password) VALUES (?, ?, ?, ?, ?)";
        String insertStudent = "INSERT INTO student (user_id) VALUES (?)";

        try (Connection c = Data_base.getConnection()) {
            c.setAutoCommit(false);
            try {
                int typeId = getStudentTypeId(c);

                int newUserId;
                try (PreparedStatement ps = c.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, typeId);
                    ps.setString(2, firstName);
                    ps.setString(3, lastName);
                    ps.setString(4, login);
                    ps.setString(5, password);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Negauti user_id raktai");
                        newUserId = keys.getInt(1);
                    }
                }

                int newStudentId;
                try (PreparedStatement ps = c.prepareStatement(insertStudent, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, newUserId);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Negauti student_id raktai");
                        newStudentId = keys.getInt(1);
                    }
                }

                c.commit();
                return newStudentId;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko pridėti studento", e);
        }
    }

    public void update(int studentId, String firstName, String lastName) {
        String getUserSql = "SELECT user_id FROM student WHERE student_id=?";
        String updUser = "UPDATE `user` SET first_name=?, last_name=?, login=?, password=? WHERE user_id=?";

        try (Connection c = Data_base.getConnection()) {
            int userId;
            try (PreparedStatement ps = c.prepareStatement(getUserSql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Studentas nerastas: id=" + studentId);
                    userId = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = c.prepareStatement(updUser)) {
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, firstName);  // login = vardas
                ps.setString(4, lastName);   // password = pavardė
                ps.setInt(5, userId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko atnaujinti studento", e);
        }
    }

    public void delete(int studentId) {
        String getUserSql = "SELECT user_id FROM student WHERE student_id=?";
        String delFromGroup = "DELETE FROM `group` WHERE student_id=?"; // gali nebūti – ok
        String delStudent = "DELETE FROM student WHERE student_id=?";
        String delUser = "DELETE FROM `user` WHERE user_id=?";

        try (Connection c = Data_base.getConnection()) {
            c.setAutoCommit(false);
            try {
                int userId;
                try (PreparedStatement ps = c.prepareStatement(getUserSql)) {
                    ps.setInt(1, studentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Studentas nerastas: id=" + studentId);
                        userId = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = c.prepareStatement(delFromGroup)) {
                    ps.setInt(1, studentId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(delStudent)) {
                    ps.setInt(1, studentId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(delUser)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko ištrinti studento", e);
        }
    }

    public List<Student> findByGroup(int groupId) {
        String sql = """
        SELECT DISTINCT s.student_id, u.user_id, u.first_name, u.last_name, u.login, u.password
        FROM `group` g
        JOIN student s ON s.student_id = g.student_id
        JOIN `user` u ON u.user_id = s.user_id
        WHERE g.group_id = ? AND g.student_id IS NOT NULL
        ORDER BY u.last_name, u.first_name
        """;
        List<Student> list = new ArrayList<>();
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                            rs.getInt("user_id"),
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getInt("student_id"),
                            groupId
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko gauti grupės studentų", e);
        }
        return list;
    }

    public void updateCredentials(int studentId, String login, String password) {
        String getUserSql = "SELECT user_id FROM student WHERE student_id=?";
        String updUser = "UPDATE `user` SET login=?, password=? WHERE user_id=?";

        try (Connection c = Data_base.getConnection()) {
            int userId;
            try (PreparedStatement ps = c.prepareStatement(getUserSql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Studentas nerastas: id=" + studentId);
                    userId = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = c.prepareStatement(updUser)) {
                ps.setString(1, login);
                ps.setString(2, password);
                ps.setInt(3, userId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            // labiausiai tikėtina klaida: duplicate key ant login (UNIQUE)
            throw new RuntimeException("Nepavyko pakeisti prisijungimo duomenų: " + e.getMessage(), e);
        }
    }

}
