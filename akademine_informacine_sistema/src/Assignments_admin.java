import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Assignments_admin extends JPanel {

    private final JDBC_group_repository groupRepo = new JDBC_group_repository();
    private final JDBC_student_repository studentRepo = new JDBC_student_repository();
    private final JDBC_subject_repository subjectRepo = new JDBC_subject_repository();

    private final JComboBox<Group> cbGroup = new JComboBox<>();
    private final JComboBox<Student> cbStudent = new JComboBox<>();
    private final JComboBox<Subject> cbSubject = new JComboBox<>();

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> listInfo = new JList<>(listModel);

    public Assignments_admin() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        int r = 0;

        gc.gridx = 0; gc.gridy = r; add(new JLabel("Grupė:"), gc);
        gc.gridx = 1; add(cbGroup, gc); r++;

        gc.gridx = 0; gc.gridy = r; add(new JLabel("Studentas:"), gc);
        gc.gridx = 1; add(cbStudent, gc); r++;

        gc.gridx = 0; gc.gridy = r; add(new JLabel("Dalykas:"), gc);
        gc.gridx = 1; add(cbSubject, gc); r++;

        JButton btnAssignStud = new JButton("Pridėti studentą į grupę");
        JButton btnRemoveStud = new JButton("Šalinti studentą iš grupės");
        JButton btnAssignSubj = new JButton("Pridėti dalyką grupei");
        JButton btnRemoveSubj = new JButton("Šalinti dalyką iš grupės");
        JButton btnRefresh    = new JButton("Atnaujinti sąrašus");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actions.add(btnAssignStud);
        actions.add(btnRemoveStud);
        actions.add(btnAssignSubj);
        actions.add(btnRemoveSubj);
        actions.add(btnRefresh);

        gc.gridx = 0; gc.gridy = r; gc.gridwidth = 2;
        add(actions, gc); r++;

        gc.gridx = 0; gc.gridy = r; gc.gridwidth = 2;
        add(new JScrollPane(listInfo), gc);

        loadCombos();
        loadAssignmentsInfo();

        cbGroup.addActionListener(e -> loadAssignmentsInfo());
        btnAssignStud.addActionListener(e -> assignStudentToGroup());
        btnRemoveStud.addActionListener(e -> removeStudentFromGroup());
        btnAssignSubj.addActionListener(e -> assignSubjectToGroup());
        btnRemoveSubj.addActionListener(e -> removeSubjectFromGroup());
        btnRefresh.addActionListener(e -> {
            loadCombos();
            loadAssignmentsInfo();
        });
    }

    private void loadCombos() {
        cbGroup.removeAllItems();
        for (Group g : groupRepo.findAll()) cbGroup.addItem(g);

        cbStudent.removeAllItems();
        for (Student s : studentRepo.findAll()) cbStudent.addItem(s);

        cbSubject.removeAllItems();
        for (Subject s : subjectRepo.findAll()) cbSubject.addItem(s);
    }

    private void assignStudentToGroup() {
        Group g = (Group) cbGroup.getSelectedItem();
        Student s = (Student) cbStudent.getSelectedItem();
        if (g == null || s == null) {
            msg("Pasirinkite grupę ir studentą");
            return;
        }

        String sqlCheckExistingGroup = """
        SELECT gr.group_id, gr.title
        FROM group_student gs
        JOIN `group` gr ON gr.group_id = gs.group_id
        WHERE gs.student_id = ?
        LIMIT 1
        """;

        String nextIdSql = "SELECT COALESCE(MAX(group_student_id),0)+1 FROM group_student";
        String insertSql = "INSERT INTO group_student (group_student_id, group_id, student_id) VALUES (?, ?, ?)";

        try (Connection c = Data_base.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(sqlCheckExistingGroup)) {
                ps.setInt(1, s.getStudentId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int existingGroupId = rs.getInt("group_id");
                        String existingGroupTitle = rs.getString("title");

                        if (existingGroupId == g.getId()) {
                            msg("Šis studentas jau priskirtas šiai grupei.");
                        } else {
                            msg("Šis studentas jau priskirtas grupei: " + existingGroupTitle +
                                    ".\nPirmiausia pašalinkite jį iš tos grupės Priskyrimų lange.");
                        }
                        return;
                    }
                }
            }

            int nextId;
            try (PreparedStatement ps = c.prepareStatement(nextIdSql);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                nextId = rs.getInt(1);
            }

            try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                ps.setInt(1, nextId);
                ps.setInt(2, g.getId());
                ps.setInt(3, s.getStudentId());
                ps.executeUpdate();
            }

            msg("Studentas sėkmingai priskirtas grupei.");
            loadAssignmentsInfo();

        } catch (SQLException e) {
            msg("Klaida priskiriant studentą: " + e.getMessage());
        }
    }


    private void removeStudentFromGroup() {
        Group g = (Group) cbGroup.getSelectedItem();
        Student s = (Student) cbStudent.getSelectedItem();
        if (g == null || s == null) { msg("Pasirinkite grupę ir studentą"); return; }

        String sql = "DELETE FROM group_student WHERE group_id=? AND student_id=?";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, g.getId());
            ps.setInt(2, s.getStudentId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                msg("Šis studentas šiai grupei nepriskirtas.");
            } else {
                msg("Studentas sėkmingai pašalintas iš grupės.");
            }
            loadAssignmentsInfo();
        } catch (SQLException e) {
            msg("Klaida šalinant studentą: " + e.getMessage());
        }
    }

    private void assignSubjectToGroup() {
        Group g = (Group) cbGroup.getSelectedItem();
        Subject sub = (Subject) cbSubject.getSelectedItem();
        if (g == null || sub == null) {
            msg("Pasirinkite grupę ir dalyką");
            return;
        }

        String checkSql = "SELECT COUNT(*) FROM group_subject WHERE group_id=? AND subject_id=?";
        String nextIdSql = "SELECT COALESCE(MAX(group_subject_id),0)+1 FROM group_subject";
        String insertSql = "INSERT INTO group_subject (group_subject_id, group_id, subject_id) VALUES (?, ?, ?)";

        try (Connection c = Data_base.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(checkSql)) {
                ps.setInt(1, g.getId());
                ps.setInt(2, sub.getSubjectId());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    int cnt = rs.getInt(1);
                    if (cnt > 0) {
                        msg("Šis dalykas jau priskirtas šiai grupei.");
                        return;
                    }
                }
            }

            int nextId;
            try (PreparedStatement ps = c.prepareStatement(nextIdSql);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                nextId = rs.getInt(1);
            }

            try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                ps.setInt(1, nextId);
                ps.setInt(2, g.getId());
                ps.setInt(3, sub.getSubjectId());
                ps.executeUpdate();
            }

            msg("Dalykas sėkmingai priskirtas grupei.");
            loadAssignmentsInfo();

        } catch (SQLException e) {
            msg("Klaida priskiriant dalyką: " + e.getMessage());
        }
    }

    private void removeSubjectFromGroup() {
        Group g = (Group) cbGroup.getSelectedItem();
        Subject sub = (Subject) cbSubject.getSelectedItem();
        if (g == null || sub == null) { msg("Pasirinkite grupę ir dalyką"); return; }

        String sql = "DELETE FROM group_subject WHERE group_id=? AND subject_id=?";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, g.getId());
            ps.setInt(2, sub.getSubjectId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                msg("Šis dalykas šiai grupei nepriskirtas.");
            } else {
                msg("Dalykas sėkmingai pašalintas iš grupės.");
            }
            loadAssignmentsInfo();
        } catch (SQLException e) {
            msg("Klaida šalinant dalyką: " + e.getMessage());
        }
    }

    private void loadAssignmentsInfo() {
        listModel.clear();
        Group g = (Group) cbGroup.getSelectedItem();
        if (g == null) return;

        listModel.addElement("Grupė: " + g.getTitle());
        listModel.addElement("");

        List<String> students = getStudentsInGroup(g.getId());
        listModel.addElement("Studentai šioje grupėje:");
        if (students.isEmpty()) {
            listModel.addElement("  (nėra priskirtų studentų)");
        } else {
            for (String s : students) listModel.addElement("  • " + s);
        }
        listModel.addElement("");

        List<String> subjects = getSubjectsInGroup(g.getId());
        listModel.addElement("Dalykai šioje grupėje:");
        if (subjects.isEmpty()) {
            listModel.addElement("  (nėra priskirtų dalykų)");
        } else {
            for (String s : subjects) listModel.addElement("  • " + s);
        }
    }

    private List<String> getStudentsInGroup(int groupId) {
        List<String> list = new ArrayList<>();
        String sql = """
            SELECT u.first_name, u.last_name
            FROM group_student gs
            JOIN student s ON s.student_id = gs.student_id
            JOIN `user` u ON u.user_id = s.user_id
            WHERE gs.group_id = ?
            ORDER BY u.last_name, u.first_name
            """;
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("first_name") + " " + rs.getString("last_name"));
                }
            }
        } catch (SQLException e) {
            msg("Klaida skaitant grupės studentus: " + e.getMessage());
        }
        return list;
    }

    private List<String> getSubjectsInGroup(int groupId) {
        List<String> list = new ArrayList<>();
        String sql = """
            SELECT st.title AS subject_title, sb.credits
            FROM group_subject gs
            JOIN subject sb ON sb.subject_id = gs.subject_id
            JOIN subject_type st ON st.sub_type_id = sb.sub_type_id
            WHERE gs.group_id = ?
            ORDER BY st.title
            """;
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("subject_title") + " (" + rs.getInt("credits") + " kr.)");
                }
            }
        } catch (SQLException e) {
            msg("Klaida skaitant grupės dalykus: " + e.getMessage());
        }
        return list;
    }

    private void msg(String s) { JOptionPane.showMessageDialog(this, s); }
}
