import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Professors_admin extends JPanel {

    private final JDBC_professor_repository repo = new JDBC_professor_repository();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Vardas", "Pavardė", "Login", "Slaptažodis"}, 0
    );
    private final JTable table = new JTable(model);

    public Professors_admin() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton add = new JButton("Pridėti");
        JButton edit = new JButton("Redaguoti vardą/pavardę");
        JButton cred = new JButton("Keisti prisijungimą");
        JButton del = new JButton("Šalinti");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.add(add); actions.add(edit); actions.add(cred); actions.add(del);
        add(actions, BorderLayout.SOUTH);

        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEditName());
        cred.addActionListener(e -> onEditCredentials());
        del.addActionListener(e -> onDelete());

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        List<Professor> professors = repo.findAll();
        for (Professor p : professors) {
            model.addRow(new Object[]{
                    p.getProfessorId(),
                    p.getFirstName(),
                    p.getLastName(),
                    p.getLogin(),
                    p.getPassword()
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

        int ok = JOptionPane.showConfirmDialog(this, msg, "Naujas dėstytojas", JOptionPane.OK_CANCEL_OPTION);
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
            JOptionPane.showMessageDialog(this, "Pasirinkite dėstytoją!");
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

        int ok = JOptionPane.showConfirmDialog(this, msg, "Redaguoti dėstytoją", JOptionPane.OK_CANCEL_OPTION);
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
            JOptionPane.showMessageDialog(this, "Pasirinkite dėstytoją!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Ar tikrai norite ištrinti pasirinktą dėstytoją?",
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
                        "Negalima ištrinti dėstytojo",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }
    private void onEditCredentials() {
        int row = table.getSelectedRow(); if (row==-1){JOptionPane.showMessageDialog (this,"Pasirinkite dėstytoją"); return; }
        int id = (int) model.getValueAt(row, 0);
        String curLogin = String.valueOf(model.getValueAt(row, 3));
        String curPass  = String.valueOf(model.getValueAt(row, 4));

        JTextField tfLogin = new JTextField(curLogin);
        JTextField tfPass  = new JTextField(curPass);
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Login:", tfLogin, "Slaptažodis:", tfPass},
                "Keisti prisijungimą", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String login = tfLogin.getText().trim();
            String pass  = tfPass.getText().trim();
            if (login.isEmpty() || pass.isEmpty()) {JOptionPane.showMessageDialog (this,"Login ir slaptažodis privalomi"); return; }
            try {
                repo.updateCredentials(id, login, pass);
                refresh();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this,"Klaida: " + ex.getMessage());
            }
        }
    }
}
