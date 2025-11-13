import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Subjects_admin extends JPanel {

    private final JDBC_subject_repository subjectRepo = new JDBC_subject_repository();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"subject_id", "Tipas", "Kreditai"}, 0
    );
    private final JTable table = new JTable(model);

    public Subjects_admin() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addBtn       = new JButton(" Pridėti dalyką");
        JButton editBtn      = new JButton(" Redaguoti dalyką");
        JButton deleteBtn    = new JButton(" Šalinti dalyką");
        JButton addTypeBtn   = new JButton(" Pridėti tipą");

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        buttons.add(addTypeBtn);
        add(buttons, BorderLayout.SOUTH);

        refresh();

        addBtn.addActionListener(e -> addSubject());
        editBtn.addActionListener(e -> editSubject());
        deleteBtn.addActionListener(e -> deleteSubject());
        addTypeBtn.addActionListener(e -> addSubjectType());
    }

    private void refresh() {
        model.setRowCount(0);
        List<Subject> subjects = subjectRepo.findAll();
        for (Subject s : subjects) {
            model.addRow(new Object[]{
                    s.getSubjectId(),
                    s.getSubTypeTitle(),   
                    s.getCredits()
            });
        }
    }

    private static class SubjectTypeOption {
        int id;
        String title;

        SubjectTypeOption(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public String toString() {
            return title;
        }
    }

    private List<SubjectTypeOption> loadSubjectTypes() {
        List<SubjectTypeOption> list = new ArrayList<>();
        String sql = "SELECT sub_type_id, title FROM subject_type ORDER BY title";
        try (Connection c = Data_base.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new SubjectTypeOption(
                        rs.getInt("sub_type_id"),
                        rs.getString("title")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Klaida skaitant tipų sąrašą: " + e.getMessage());
        }
        return list;
    }

    private void addSubjectType() {
        String title = JOptionPane.showInputDialog(this,
                "Įveskite naujo tipo pavadinimą:", "Naujas tipas",
                JOptionPane.PLAIN_MESSAGE);
        if (title == null) return;
        title = title.trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pavadinimas negali būti tuščias.");
            return;
        }

        String nextIdSql = "SELECT COALESCE(MAX(sub_type_id),0)+1 FROM subject_type";
        String insertSql = "INSERT INTO subject_type (sub_type_id, title) VALUES (?, ?)";

        try (Connection c = Data_base.getConnection()) {
            int nextId;
            try (PreparedStatement ps = c.prepareStatement(nextIdSql);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                nextId = rs.getInt(1);
            }
            try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                ps.setInt(1, nextId);
                ps.setString(2, title);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Tipas sėkmingai pridėtas.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Klaida pridedant tipą: " + e.getMessage());
        }
    }

    private void addSubject() {
        List<SubjectTypeOption> types = loadSubjectTypes();
        if (types.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pirmiausia sukurkite bent vieną tipą (mygtukas \"Pridėti tipą\").");
            return;
        }

        JComboBox<SubjectTypeOption> cbType = new JComboBox<>(types.toArray(new SubjectTypeOption[0]));
        JTextField tfCredits = new JTextField();

        Object[] msg = {
                "Tipas:", cbType,
                "Kreditai:", tfCredits
        };

        int ok = JOptionPane.showConfirmDialog(this, msg,
                "Naujas dalykas", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            SubjectTypeOption selType = (SubjectTypeOption) cbType.getSelectedItem();
            String creditsStr = tfCredits.getText().trim();

            if (selType == null || creditsStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Visi laukai privalomi.");
                return;
            }

            int credits;
            try {
                credits = Integer.parseInt(creditsStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Kreditai turi būti skaičius.");
                return;
            }

            String nextIdSql = "SELECT COALESCE(MAX(subject_id),0)+1 FROM subject";
            String insertSql = "INSERT INTO subject (subject_id, sub_type_id, credits) VALUES (?, ?, ?)";

            try (Connection c = Data_base.getConnection()) {
                int nextId;
                try (PreparedStatement ps = c.prepareStatement(nextIdSql);
                     ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    nextId = rs.getInt(1);
                }
                try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                    ps.setInt(1, nextId);
                    ps.setInt(2, selType.id);
                    ps.setInt(3, credits);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Dalykas sėkmingai sukurtas.");
                refresh();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Klaida pridedant dalyką: " + e.getMessage());
            }
        }
    }

    private void editSubject() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pasirinkite dalyką!");
            return;
        }

        int subjectId = (int) model.getValueAt(row, 0);

        // Gaunam esamą dalyką iš DB, kad žinotume cur sub_type_id
        String sql = "SELECT sub_type_id, credits FROM subject WHERE subject_id = ?";

        try (Connection c = Data_base.getConnection();
             PreparedStatement ps0 = c.prepareStatement(sql)) {

            ps0.setInt(1, subjectId);
            int currentSubTypeId;
            int currentCredits;

            try (ResultSet rs = ps0.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this,
                            "Nepavyko rasti dalyko DB.");
                    return;
                }
                currentSubTypeId = rs.getInt("sub_type_id");
                currentCredits   = rs.getInt("credits");
            }

            List<SubjectTypeOption> types = loadSubjectTypes();
            if (types.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nėra galimų tipų. Pirmiausia sukurkite tipą.");
                return;
            }

            JComboBox<SubjectTypeOption> cbType = new JComboBox<>(types.toArray(new SubjectTypeOption[0]));
            SubjectTypeOption toSelect = null;
            for (SubjectTypeOption t : types) {
                if (t.id == currentSubTypeId) {
                    toSelect = t;
                    break;
                }
            }
            if (toSelect != null) cbType.setSelectedItem(toSelect);

            JTextField tfCredits = new JTextField(String.valueOf(currentCredits));

            Object[] msg = {
                    "Tipas:", cbType,
                    "Kreditai:", tfCredits
            };

            int ok = JOptionPane.showConfirmDialog(this, msg,
                    "Redaguoti dalyką", JOptionPane.OK_CANCEL_OPTION);

            if (ok == JOptionPane.OK_OPTION) {
                SubjectTypeOption selType = (SubjectTypeOption) cbType.getSelectedItem();
                String creditsStr = tfCredits.getText().trim();

                if (selType == null || creditsStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Visi laukai privalomi.");
                    return;
                }

                int credits;
                try {
                    credits = Integer.parseInt(creditsStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Kreditai turi būti skaičius.");
                    return;
                }

                String updateSql = "UPDATE subject SET sub_type_id=?, credits=? WHERE subject_id=?";

                try (PreparedStatement ps = c.prepareStatement(updateSql)) {
                    ps.setInt(1, selType.id);
                    ps.setInt(2, credits);
                    ps.setInt(3, subjectId);
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Dalykas atnaujintas.");
                refresh();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Klaida redaguojant dalyką: " + e.getMessage());
        }
    }

    private void deleteSubject() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pasirinkite dalyką!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Ar tikrai norite ištrinti pasirinktą dalyką?",
                "Patvirtinimas",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                subjectRepo.delete(id);
                refresh();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Negalima ištrinti dalyko",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }
}
