import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Assignments_admin extends JPanel {

    private final JDBC_group_repository groupRepo = new JDBC_group_repository();
    private final JDBC_student_repository studentRepo = new JDBC_student_repository();
    private final JDBC_subject_repository subjectRepo = new JDBC_subject_repository();

    private final JComboBox<Group> cbGroup   = new JComboBox<>();
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

        JButton btnStudToGroup = new JButton("Priskirti studentą grupei");
        JButton btnSubjToGroup = new JButton("Priskirti dalyką grupei");
        JButton btnRemoveStud  = new JButton("Pašalinti studentą iš grupės");
        JButton btnRemoveSubj  = new JButton("Pašalinti dalyką iš grupės");
        JButton btnRefreshInfo = new JButton("Rodyti priskyrimus");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.add(btnStudToGroup);
        actions.add(btnSubjToGroup);
        actions.add(btnRemoveStud);
        actions.add(btnRemoveSubj);
        actions.add(btnRefreshInfo);

        gc.gridx = 0; gc.gridy = r; gc.gridwidth = 2; add(actions, gc); r++;

        gc.gridx = 0; gc.gridy = r; gc.gridwidth = 2;
        add(new JScrollPane(listInfo), gc);

        loadCombos();

        cbGroup.addActionListener(e -> loadAssignmentsInfo());

        btnStudToGroup.addActionListener(e -> assignStudentToGroup());
        btnSubjToGroup.addActionListener(e -> assignSubjectToGroup());
        btnRemoveStud.addActionListener(e -> removeStudentFromGroup());
        btnRemoveSubj.addActionListener(e -> removeSubjectFromGroup());
        btnRefreshInfo.addActionListener(e -> loadAssignmentsInfo());
    }

    private void loadCombos() {
        cbGroup.removeAllItems();
        for (Group g : groupRepo.findAll()) cbGroup.addItem(g);

        cbStudent.removeAllItems();
        for (Student s : studentRepo.findAll()) cbStudent.addItem(s);

        cbSubject.removeAllItems();
        for (Subject s : subjectRepo.findAll()) cbSubject.addItem(s);

        loadAssignmentsInfo();
    }

    private void assignStudentToGroup() {
        Group g = (Group) cbGroup.getSelectedItem();
        Student s = (Student) cbStudent.getSelectedItem();
        if (g == null || s == null) { msg("Pasirinkite grupę ir studentą"); return; }

        String sql = "INSERT INTO `group` (group_id, student_id, subject_id, title) VALUES (?, ?, NULL, ?)";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, g.getId());
            ps.setInt(2, s.getStudentId());
            ps.setString(3, g.getTitle());
            ps.executeUpdate();
            msg("Studentas priskirtas grupei.");
            loadAssignmentsInfo();
        } catch (SQLException e) {
            msg("Klaida: " + e.getMessage());
        }
    }

    private void assignSubjectToGroup() {
        Group g = (Group) cbGroup.getSelectedItem();
        Subject sub = (Subject) cbSubject.getSelectedItem();
        if (g == null || sub == null) { msg("Pasirinkite grupę ir dalyką"); return; }

        String sql = "INSERT INTO `group` (group_id, student_id, subject_id, title) VALUES (?, NULL, ?, ?)";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, g.getId());
            ps.setInt(2, sub.getSubjectId());
            ps.setString(3, g.getTitle());
            ps.executeUpdate();
            msg("Dalykas priskirtas grupei.");
            loadAssignmentsInfo();
        } catch (SQLException e) {
            msg("Klaida: " + e.getMessage());
        }
    }

    private void removeStudentFromGroup() {
        Group g = (Group) cbGroup.getSelectedItem();
        Student s = (Student) cbStudent.getSelectedItem();
        if (g == null || s == null) { msg("Pasirinkite grupę ir studentą"); return; }

        String sql = "DELETE FROM `group` WHERE group_id=? AND student_id=?";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, g.getId());
            ps.setInt(2, s.getStudentId());
            int n = ps.executeUpdate();
            msg(n > 0 ? "Studentas pašalintas iš grupės." : "Įrašas nerastas.");
            loadAssignmentsInfo();
        } catch (SQLException e) {
            msg("Klaida: " + e.getMessage());
        }
    }

    private void removeSubjectFromGroup() {
        Group g = (Group) cbGroup.getSelectedItem();
        Subject sub = (Subject) cbSubject.getSelectedItem();
        if (g == null || sub == null) { msg("Pasirinkite grupę ir dalyką"); return; }

        String sql = "DELETE FROM `group` WHERE group_id=? AND subject_id=?";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, g.getId());
            ps.setInt(2, sub.getSubjectId());
            int n = ps.executeUpdate();
            msg(n > 0 ? "Dalykas nuimtas nuo grupės." : "Įrašas nerastas.");
            loadAssignmentsInfo();
        } catch (SQLException e) {
            msg("Klaida: " + e.getMessage());
        }
    }

    private void loadAssignmentsInfo() {
        listModel.clear();
        Group g = (Group) cbGroup.getSelectedItem();
        if (g == null) return;

        String studSql = """
            SELECT u.first_name, u.last_name
            FROM `group` gr
            JOIN student s ON s.student_id = gr.student_id
            JOIN `user` u ON u.user_id = s.user_id
            WHERE gr.group_id = ? AND gr.student_id IS NOT NULL
            ORDER BY u.last_name, u.first_name
            """;

        String subjSql = """
            SELECT st.title AS subject_title, sb.credits
            FROM `group` gr
            JOIN subject sb ON sb.subject_id = gr.subject_id
            JOIN subject_type st ON st.sub_type_id = sb.sub_type_id
            WHERE gr.group_id = ? AND gr.subject_id IS NOT NULL
            ORDER BY subject_title
            """;

        try (Connection c = Data_base.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(studSql)) {
                ps.setInt(1, g.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    listModel.addElement("— Studentai grupėje " + g.getTitle() + ":");
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        listModel.addElement("   • " + rs.getString("first_name") + " " + rs.getString("last_name"));
                    }
                    if (!any) listModel.addElement("   (nėra)");
                    listModel.addElement("");
                }
            }
            try (PreparedStatement ps = c.prepareStatement(subjSql)) {
                ps.setInt(1, g.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    listModel.addElement("— Dalykai grupėje " + g.getTitle() + ":");
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        listModel.addElement("   • " + rs.getString("subject_title") +
                                " (" + rs.getInt("credits") + " kr.)");
                    }
                    if (!any) listModel.addElement("   (nėra)");
                }
            }
        } catch (SQLException e) {
            msg("Klaida: " + e.getMessage());
        }
    }

    private void msg(String s) { JOptionPane.showMessageDialog(this, s); }
}
