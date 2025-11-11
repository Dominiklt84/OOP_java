import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Groups_admin extends JPanel {
    private final JDBC_group_repository repo = new JDBC_group_repository();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Pavadinimas"}, 0);
    private final JTable table = new JTable(model);

    public Groups_admin() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addBtn = new JButton(" Pridėti");
        JButton editBtn = new JButton(" Redaguoti");
        JButton deleteBtn = new JButton(" Šalinti");

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        add(buttons, BorderLayout.SOUTH);

        refresh();

        addBtn.addActionListener(e -> addGroup());
        editBtn.addActionListener(e -> editGroup());
        deleteBtn.addActionListener(e -> deleteGroup());
    }

    private void refresh() {
        model.setRowCount(0);
        List<Group> groups = repo.findAll();
        for (Group g : groups) {
            model.addRow(new Object[]{g.getId(), g.getTitle()});
        }
    }

    private void addGroup() {
        String name = JOptionPane.showInputDialog(this, "Įveskite grupės pavadinimą:");
        if (name != null && !name.isBlank()) {
            repo.add(new Group(0, name.trim()));
            refresh();
        }
    }

    private void editGroup() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pasirinkite grupę!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String oldName = (String) model.getValueAt(row, 1);
        String newName = JOptionPane.showInputDialog(this, "Naujas pavadinimas:", oldName);
        if (newName != null && !newName.isBlank()) {
            repo.update(new Group(id, newName.trim()));
            refresh();
        }
    }

    private void deleteGroup() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pasirinkite grupę!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Ar tikrai norite ištrinti?", "Patvirtinimas", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            repo.delete(id);
            refresh();
        }
    }
}
