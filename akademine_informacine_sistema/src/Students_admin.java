import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Students_admin extends JPanel {
    private final JDBC_student_repository repo = new JDBC_student_repository ();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"student_id", "Vardas", "Pavardė", "Login", "Slaptažodis"}, 0) {
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    public Students_admin() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addBtn = new JButton("Pridėti");
        JButton editBtn = new JButton("Redaguoti");
        JButton deleteBtn = new JButton("Šalinti");

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        add(buttons, BorderLayout.SOUTH);

        refresh();

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        deleteBtn.addActionListener(e -> onDelete());
    }

    private void refresh() {
        model.setRowCount(0);
        List<Student> list = repo.findAll();
        for (Student s : list) {
            model.addRow(new Object[]{
                    s.getStudentId(),
                    s.getFirstName(),
                    s.getLastName(),
                    s.getLogin(),
                    s.getPassword()
            });
        }
    }

    private void onAdd() {
        JTextField fName = new JTextField();
        JTextField lName = new JTextField();
        Object[] msg = {"Vardas:", fName, "Pavardė:", lName};
        int ok = JOptionPane.showConfirmDialog(this, msg, "Naujas studentas", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String fn = fName.getText().trim();
            String ln = lName.getText().trim();
            if (fn.isEmpty() || ln.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vardas ir pavardė privalomi");
                return;
            }
            repo.add(fn, ln);
            refresh();
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pasirinkite studentą");
            return;
        }
        int studentId = (int) model.getValueAt(row, 0);
        String curF = String.valueOf(model.getValueAt(row, 1));
        String curL = String.valueOf(model.getValueAt(row, 2));

        JTextField fName = new JTextField(curF);
        JTextField lName = new JTextField(curL);
        Object[] msg = {"Vardas:", fName, "Pavardė:", lName};
        int ok = JOptionPane.showConfirmDialog(this, msg, "Redaguoti studentą", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String fn = fName.getText().trim();
            String ln = lName.getText().trim();
            if (fn.isEmpty() || ln.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vardas ir pavardė privalomi");
                return;
            }
            repo.update(studentId, fn, ln);
            refresh();
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pasirinkite studentą");
            return;
        }
        int studentId = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Ar tikrai ištrinti?", "Patvirtinimas", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            repo.delete(studentId);
            refresh();
        }
    }
}
