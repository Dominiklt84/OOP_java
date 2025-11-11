import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Professors_admin extends JPanel {
    private final JDBC_professor_repository repo = new JDBC_professor_repository();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"professor_id", "Vardas", "Pavardė", "Login", "Slaptažodis"}, 0){
        public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable table = new JTable(model);

    public Professors_admin() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton add = new JButton(" Pridėti");
        JButton edit = new JButton(" Redaguoti");
        JButton del = new JButton(" Šalinti");
        actions.add(add); actions.add(edit); actions.add(del);
        add(actions, BorderLayout.SOUTH);

        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEdit());
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
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Vardas:", fn, "Pavardė:", ln}, "Naujas dėstytojas", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            if (fn.getText().isBlank() || ln.getText().isBlank()) { msg("Užpildykite laukus"); return; }
            repo.add(fn.getText().trim(), ln.getText().trim());
            refresh();
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow(); if (row==-1){ msg("Pasirinkite dėstytoją"); return; }
        int id = (int) model.getValueAt(row, 0);
        JTextField fn = new JTextField(String.valueOf(model.getValueAt(row,1)));
        JTextField ln = new JTextField(String.valueOf(model.getValueAt(row,2)));
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Vardas:", fn, "Pavardė:", ln}, "Redaguoti", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            if (fn.getText().isBlank() || ln.getText().isBlank()) { msg("Užpildykite laukus"); return; }
            repo.update(id, fn.getText().trim(), ln.getText().trim());
            refresh();
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow(); if (row==-1){ msg("Pasirinkite dėstytoją"); return; }
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Ištrinti?", "Patvirtinimas", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            repo.delete(id); refresh();
        }
    }
    private void msg(String s){ JOptionPane.showMessageDialog(this, s); }
}
