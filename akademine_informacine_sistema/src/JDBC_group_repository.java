import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JDBC_group_repository implements Group_repository {

    public List<Group> findAll() {
        List<Group> list = new ArrayList<>();
        String sql = "SELECT * FROM grupes ORDER BY group_id";
        try (Connection c = Data_base.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Group(rs.getInt("group_id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko gauti grupių sąrašo", e);
        }
        return list;
    }

    public Optional<Group> findById(int id) {
        String sql = "SELECT * FROM grupes WHERE group_id=?";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new Group(rs.getInt("group_id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void add(Group group) {
        String sql = "INSERT INTO grupes (name) VALUES (?)";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, group.getTitle());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko pridėti grupės", e);
        }
    }

    public void update(Group group) {
        String sql = "UPDATE grupes SET name=? WHERE group_id=?";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, group.getTitle());
            ps.setInt(2, group.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko atnaujinti grupės", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM grupes WHERE group_id=?";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nepavyko ištrinti grupės", e);
        }
    }
}
