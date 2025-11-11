import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Professors_admin extends JPanel {
    private final JDBC_professor_repository repo = new JDBC_professor_repository();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"professor_id", "Vardas", "Pavardė", "Login", "Slaptažodis"}, 0){
        public boolean isCellEditable(int r, int c){ return false; }
    };
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
        List<Professor> list = repo.findAll();
        for (Professor p : list) {
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
        JTextField fn = new JTextField();
        JTextField ln = new JTextField();
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Vardas:", fn, "Pavardė:", ln},
                "Naujas dėstytojas", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            if (fn.getText().isBlank() || ln.getText().isBlank()) { msg("Užpildykite laukus"); return; }
            repo.add(fn.getText().trim(), ln.getText().trim());
            refresh();
        }
    }

    private void onEditName() {
        int row = table.getSelectedRow(); if (row==-1){ msg("Pasirinkite dėstytoją"); return; }
        int id = (int) model.getValueAt(row, 0);
        JTextField fn = new JTextField(String.valueOf(model.getValueAt(row,1)));
        JTextField ln = new JTextField(String.valueOf(model.getValueAt(row,2)));
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Vardas:", fn, "Pavardė:", ln},
                "Redaguoti", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            if (fn.getText().isBlank() || ln.getText().isBlank()) { msg("Užpildykite laukus"); return; }
            repo.update(id, fn.getText().trim(), ln.getText().trim());
            refresh();
        }
    }

    private void onEditCredentials() {
        int row = table.getSelectedRow(); if (row==-1){ msg("Pasirinkite dėstytoją"); return; }
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
            if (login.isEmpty() || pass.isEmpty()) { msg("Login ir slaptažodis privalomi"); return; }
            try {
                repo.updateCredentials(id, login, pass);
                refresh();
            } catch (RuntimeException ex) {
                msg("Klaida: " + ex.getMessage());
            }
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow(); if (row==-1){ msg("Pasirinkite dėstytoją"); return; }
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Ištrinti?", "Patvirtinimas",
                JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            repo.delete(id); refresh();
        }
    }

    private void msg(String s){ JOptionPane.showMessageDialog(this, s); }
}
