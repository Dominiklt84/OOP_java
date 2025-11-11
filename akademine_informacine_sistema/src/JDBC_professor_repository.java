import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JDBC_professor_repository implements Professor_repository {

    private int getProfessorTypeId(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT user_type_id FROM `user_type` WHERE title='PROFESSOR'")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("user_type neturi 'PROFESSOR'");
            }
        }
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

        String insUser = "INSERT INTO `user` (user_type_id, first_name, last_name, login, password) VALUES (?, ?, ?, ?, ?)";
        String insProf = "INSERT INTO professor (user_id) VALUES (?)";

        try (Connection c = Data_base.getConnection()) {
            c.setAutoCommit(false);
            try {
                int typeId = getProfessorTypeId(c);

                int newUserId;
                try (PreparedStatement ps = c.prepareStatement(insUser, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, typeId);
                    ps.setString(2, firstName);
                    ps.setString(3, lastName);
                    ps.setString(4, login);
                    ps.setString(5, password);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Negauti user_id");
                        newUserId = keys.getInt(1);
                    }
                }

                int newProfId;
                try (PreparedStatement ps = c.prepareStatement(insProf, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, newUserId);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Negauti professor_id");
                        newProfId = keys.getInt(1);
                    }
                }

                c.commit();
                return newProfId;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko pridėti dėstytojo", e);
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
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko ištrinti dėstytojo", e);
        }
    }
}
