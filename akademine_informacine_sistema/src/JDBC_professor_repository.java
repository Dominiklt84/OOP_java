import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.SQLIntegrityConstraintViolationException;

public class JDBC_professor_repository implements Professor_repository {

    private int getProfessorTypeId(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT user_type_id FROM `user_type` WHERE UPPER(title)=UPPER('PROFESSOR') LIMIT 1")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("user_type neturi 'PROFESSOR' įrašo");
    }

    public List<Professor> findAll() {
        String sql = """
            SELECT p.professor_id,
                   u.user_id, u.first_name, u.last_name, u.login, u.password
            FROM professor p
            JOIN `user` u ON u.user_id = p.user_id
            ORDER BY p.professor_id
            """;
        List<Professor> list = new ArrayList<>();
        try (Connection c = Data_base.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Professor(
                        rs.getInt("user_id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("professor_id")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko nuskaityti dėstytojų", e);
        }
        return list;
    }

    public Optional<Professor> findByProfessorId(int professorId) {
        String sql = """
            SELECT p.professor_id,
                   u.user_id, u.first_name, u.last_name, u.login, u.password
            FROM professor p
            JOIN `user` u ON u.user_id = p.user_id
            WHERE p.professor_id=?
            """;
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, professorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Professor(
                            rs.getInt("user_id"),
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getInt("professor_id")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko rasti dėstytojo", e);
        }
        return Optional.empty();
    }

    public int add(String firstName, String lastName) {
        String login = firstName;
        String password = lastName;

        String getNextUserIdSql = "SELECT COALESCE(MAX(user_id),0)+1 AS next_id FROM `user`";
        String insertUser       = "INSERT INTO `user` (user_id, user_type_id, first_name, last_name, login, password) VALUES (?, ?, ?, ?, ?, ?)";
        String insertProf       = "INSERT INTO professor (professor_id, user_id, group_id) VALUES (?, ?, NULL)";

        try (Connection c = Data_base.getConnection()) {
            c.setAutoCommit(false);
            try {
                int typeId = getProfessorTypeId(c);

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

                int nextProfId = 1;
                try (Statement st = c.createStatement();
                     ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(professor_id),0)+1 AS next_id FROM professor")) {
                    if (rs.next()) nextProfId = rs.getInt("next_id");
                }

                try (PreparedStatement ps = c.prepareStatement(insertProf)) {
                    ps.setInt(1, nextProfId);
                    ps.setInt(2, nextUserId);
                    ps.executeUpdate();
                }

                c.commit();
                return nextProfId;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko pridėti dėstytojo: " + e.getMessage(), e);
        }
    }

    public void update(int professorId, String firstName, String lastName) {
        String getUser = "SELECT user_id FROM professor WHERE professor_id=?";
        String updUser = "UPDATE `user` SET first_name=?, last_name=?, login=?, password=? WHERE user_id=?";
        try (Connection c = Data_base.getConnection()) {
            int userId;
            try (PreparedStatement ps = c.prepareStatement(getUser)) {
                ps.setInt(1, professorId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Dėstytojas nerastas: id=" + professorId);
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
            throw new RuntimeException("Nepavyko atnaujinti dėstytojo", e);
        }
    }

    public void updateCredentials(int professorId, String login, String password) {
        String getUser = "SELECT user_id FROM professor WHERE professor_id=?";
        String updUser = "UPDATE `user` SET login=?, password=? WHERE user_id=?";
        try (Connection c = Data_base.getConnection()) {
            int userId;
            try (PreparedStatement ps = c.prepareStatement(getUser)) {
                ps.setInt(1, professorId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Dėstytojas nerastas: id=" + professorId);
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

    public void delete(int professorId) {
        String getUser = "SELECT user_id FROM professor WHERE professor_id=?";
        String delProf = "DELETE FROM professor WHERE professor_id=?";
        String delUser = "DELETE FROM `user` WHERE user_id=?";

        try (Connection c = Data_base.getConnection()) {
            c.setAutoCommit(false);
            try {
                int userId;

                try (PreparedStatement ps = c.prepareStatement(getUser)) {
                    ps.setInt(1, professorId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Dėstytojas nerastas: id=" + professorId);
                        userId = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = c.prepareStatement(delProf)) {
                    ps.setInt(1, professorId);
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
                        "Negalima ištrinti dėstytojo.\n" +
                                "Pirma:\n" +
                                " • ištrinkite ar pakeiskite pažymius, kuriuos šis dėstytojas yra suvedęs,\n" +
                                " • atjunkite dėstytoją nuo grupės (jei priskirtas),\n" +
                                " • įsitikinkite, kad nėra kitų ryšių su šiuo dėstytoju.\n\n" +
                                "Tada bandykite trinti dar kartą.", e);
            } catch (SQLException e) {
                c.rollback();
                throw new RuntimeException("Nepavyko ištrinti dėstytojo: " + e.getMessage(), e);
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko ištrinti dėstytojo: " + e.getMessage(), e);
        }
    }

}
