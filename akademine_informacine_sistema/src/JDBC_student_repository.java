import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLIntegrityConstraintViolationException;

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

    private int getStudentTypeId(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT user_type_id FROM `user_type` WHERE UPPER(title)=UPPER('STUDENT') LIMIT 1")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Lentelėje user_type nerasta 'STUDENT'. Įterpk įrašą.");
    }

    public int add(String firstName, String lastName) {
        String login = firstName;
        String password = lastName;

        String getNextUserIdSql = "SELECT COALESCE(MAX(user_id),0)+1 AS next_id FROM `user`";
        String insertUser       = "INSERT INTO `user` (user_id, user_type_id, first_name, last_name, login, password) VALUES (?, ?, ?, ?, ?, ?)";
        String insertStudent    = "INSERT INTO student (student_id, user_id) VALUES (?, ?)";

        try (Connection c = Data_base.getConnection()) {
            c.setAutoCommit(false);
            try {
                int typeId = getStudentTypeId(c);

                int nextUserId = 1;
                try (Statement st = c.createStatement();
                     ResultSet rs = st.executeQuery(getNextUserIdSql)) {
                    if (rs.next()) nextUserId = rs.getInt("next_id");
                }

                try (PreparedStatement ps = c.prepareStatement(insertUser)) {
                    ps.setInt(1, nextUserId);
                    ps.setInt(2, typeId);
                    ps.setString(3, firstName);
                    ps.setString(4, lastName);
                    ps.setString(5, login);
                    ps.setString(6, password);
                    ps.executeUpdate();
                }

                int nextStudentId = 1;
                try (Statement st = c.createStatement();
                     ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(student_id),0)+1 AS next_id FROM student")) {
                    if (rs.next()) nextStudentId = rs.getInt("next_id");
                }

                try (PreparedStatement ps = c.prepareStatement(insertStudent)) {
                    ps.setInt(1, nextStudentId);
                    ps.setInt(2, nextUserId);
                    ps.executeUpdate();
                }

                c.commit();
                return nextStudentId;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko pridėti studento: " + e.getMessage(), e);
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
                ps.setString(3, firstName);
                ps.setString(4, lastName);
                ps.setInt(5, userId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko atnaujinti studento", e);
        }
    }

    public void updateCredentials(int studentId, String login, String password) {
        String getUserSql = "SELECT user_id FROM student WHERE student_id=?";
        String updUser    = "UPDATE `user` SET login=?, password=? WHERE user_id=?";

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
            throw new RuntimeException("Nepavyko pakeisti prisijungimo duomenų: " + e.getMessage(), e);
        }
    }

    public void delete(int studentId) {
        String getUserSql = "SELECT user_id FROM student WHERE student_id=?";
        String delStudent = "DELETE FROM student WHERE student_id=?";
        String delUser    = "DELETE FROM `user` WHERE user_id=?";

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

                try (PreparedStatement ps = c.prepareStatement(delStudent)) {
                    ps.setInt(1, studentId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = c.prepareStatement(delUser)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                c.commit();
            } catch (SQLIntegrityConstraintViolationException e) {
                c.rollback();
                throw new RuntimeException(
                        "Negalima ištrinti studento.\n" +
                                "Pirma:\n" +
                                " • ištrinkite arba pakoreguokite visus šio studento pažymius,\n" +
                                " • pašalinkite studentą iš grupių (Assignments skiltyje),\n" +
                                " • įsitikinkite, kad nėra kitų ryšių su šiuo studentu.\n\n" +
                                "Tada bandykite trinti dar kartą.", e);
            } catch (SQLException e) {
                c.rollback();
                throw new RuntimeException("Nepavyko ištrinti studento: " + e.getMessage(), e);
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko ištrinti studento: " + e.getMessage(), e);
        }
    }


    public List<Student> findByGroup(int groupId) {
        String sql = """
        SELECT DISTINCT s.student_id, u.user_id, u.first_name, u.last_name, u.login, u.password
        FROM group_student gs
        JOIN student s ON s.student_id = gs.student_id
        JOIN `user` u ON u.user_id = s.user_id
        WHERE gs.group_id = ?
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

}
