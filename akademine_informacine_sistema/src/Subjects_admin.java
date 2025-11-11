import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Subjects_admin extends JPanel {
    private final JDBC_subject_repository repo = new JDBC_subject_repository();
    private final JDBC_subject_type_repository typeRepo = new JDBC_subject_type_repository();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"subject_id", "Tipas", "credits", "sub_type_id"}, 0) {
        public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable table = new JTable(model);

    public Subjects_admin() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton add = new JButton("Pridėti");
        JButton edit = new JButton("Redaguoti");
        JButton del = new JButton("Šalinti");
        actions.add(add); actions.add(edit); actions.add(del);
        add(actions, BorderLayout.SOUTH);

        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEdit());
        del.addActionListener(e -> onDelete());

        refresh();
        table.getColumnModel().getColumn(3).setMinWidth(0);
        table.getColumnModel().getColumn(3).setMaxWidth(0);
        table.getColumnModel().getColumn(3).setWidth(0);
    }

    private void refresh() {
        model.setRowCount(0);
        List<Subject> list = repo.findAll();
        for (Subject s : list)
            model.addRow(new Object[]{s.getSubjectId(), s.getSubTypeTitle(), s.getCredits(), s.getSubTypeId()});
    }

    private void onAdd() {
        JComboBox<Subject_type> cbType = new JComboBox<>();
        for (Subject_type t : typeRepo.findAll()) cbType.addItem(t);
        JTextField tfCredits = new JTextField();

        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "Tipas:", cbType,
                "Kreditai:", tfCredits
        }, "Naujas dalykas", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                Subject_type t = (Subject_type) cbType.getSelectedItem();
                int credits = Integer.parseInt(tfCredits.getText().trim());
                repo.add(t.getSubTypeId(), credits);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Klaida: " + ex.getMessage());
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pasirinkite dalyką"); return; }

        int subjectId = (int) model.getValueAt(row, 0);
        int curSubTypeId = (int) model.getValueAt(row, 3);
        int curCredits = Integer.parseInt(String.valueOf(model.getValueAt(row, 2)));

        JComboBox<Subject_type> cbType = new JComboBox<>();
        int sel = 0, i = 0;
        for (Subject_type t : typeRepo.findAll()) {
            cbType.addItem(t);
            if (t.getSubTypeId() == curSubTypeId) sel = i;
            i++;
        }
        cbType.setSelectedIndex(sel);
        JTextField tfCredits = new JTextField(String.valueOf(curCredits));

        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "Tipas:", cbType,
                "Kreditai:", tfCredits
        }, "Redaguoti dalyką", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                Subject_type t = (Subject_type) cbType.getSelectedItem();
                int credits = Integer.parseInt(tfCredits.getText().trim());
                repo.update(subjectId, t.getSubTypeId(), credits);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Klaida: " + ex.getMessage());
            }
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pasirinkite dalyką"); return; }
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Ištrinti dalyką?", "Patvirtinimas",
                JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            repo.delete(id);
            refresh();
        }
    }
}
