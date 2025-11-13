import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Students_admin extends JPanel {

    private final JDBC_student_repository repo = new JDBC_student_repository();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Vardas", "Pavardė", "Login", "Slaptažodis"}, 0
    );
    private final JTable table = new JTable(model);

    public Students_admin() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addBtn = new JButton("Pridėti");
        JButton editBtn = new JButton("Redaguoti vardą/pavardę");
        JButton credBtn = new JButton("Keisti prisijungimą");
        JButton deleteBtn = new JButton("Šalinti");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(credBtn);
        buttons.add(deleteBtn);
        add(buttons, BorderLayout.SOUTH);

        refresh();

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEditName());
        credBtn.addActionListener(e -> onEditCredentials());
        deleteBtn.addActionListener(e -> onDelete());
    }

    private void refresh() {
        model.setRowCount(0);
        List<Student> students = repo.findAll();
        for (Student s : students) {
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

        Object[] msg = {
                "Vardas:", fName,
                "Pavardė:", lName
        };

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

    private void onEditName() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pasirinkite studentą!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String oldFirst = String.valueOf(model.getValueAt(row, 1));
        String oldLast = String.valueOf(model.getValueAt(row, 2));

        JTextField fName = new JTextField(oldFirst);
        JTextField lName = new JTextField(oldLast);
        Object[] msg = {
                "Vardas:", fName,
                "Pavardė:", lName
        };

        int ok = JOptionPane.showConfirmDialog(this, msg, "Redaguoti studentą", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String fn = fName.getText().trim();
            String ln = lName.getText().trim();
            if (fn.isEmpty() || ln.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vardas ir pavardė privalomi");
                return;
            }
            repo.update(id, fn, ln);
            refresh();
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pasirinkite studentą!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Ar tikrai norite ištrinti pasirinktą studentą?",
                "Patvirtinimas",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                repo.delete(id);
                refresh();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Negalima ištrinti studento",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }
    private void onEditCredentials() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pasirinkite studentą"); return; }
        int studentId = (int) model.getValueAt(row, 0);
        String curLogin = String.valueOf(model.getValueAt(row, 3));
        String curPass  = String.valueOf(model.getValueAt(row, 4));

        JTextField tfLogin = new JTextField(curLogin);
        JTextField tfPass  = new JTextField(curPass);
        Object[] msg = {"Login:", tfLogin, "Slaptažodis:", tfPass};
        int ok = JOptionPane.showConfirmDialog(this, msg, "Keisti prisijungimą", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String login = tfLogin.getText().trim();
            String pass  = tfPass.getText().trim();
            if (login.isEmpty() || pass.isEmpty()) { JOptionPane.showMessageDialog(this, "Login ir slaptažodis privalomi"); return; }
            try {
                repo.updateCredentials(studentId, login, pass);
                refresh();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, "Klaida: " + ex.getMessage());
            }
        }
    }
}
