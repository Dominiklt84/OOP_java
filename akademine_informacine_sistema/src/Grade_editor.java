import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class Grade_editor extends JPanel {
    private final JDBC_group_repository groupRepo = new JDBC_group_repository();
    private final JDBC_subject_repository subjectRepo = new JDBC_subject_repository();
    private final JDBC_student_repository studentRepo = new JDBC_student_repository ();
    private final Grade_service service = new Grade_service();

    private final JComboBox<Group> cbGroup = new JComboBox<>();
    private final JComboBox<Subject> cbSubject = new JComboBox<>();
    private final JTextField tfSemester = new JTextField(10);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","StudentId","SubjectId","Pažymys","Data","Semestras","Komentaras"}, 0) {
         public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable table = new JTable(model);

    private final Integer professor_id;

    public Grade_editor(Integer professor_id) {
        this.professor_id = professor_id;
        setLayout(new BorderLayout());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("Grupė:")); filters.add(cbGroup);
        filters.add(new JLabel("Dalykas:")); filters.add(cbSubject);
        filters.add(new JLabel("Semestras:")); filters.add(tfSemester);
        JButton btnLoad = new JButton("Įkelti"); filters.add(btnLoad);
        add(filters, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton add = new JButton(" Pridėti"); JButton edit = new JButton(" Redaguoti"); JButton del = new JButton("Šalinti");
        actions.add(add); actions.add(edit); actions.add(del);
        add(actions, BorderLayout.SOUTH);

        loadCombos();
        btnLoad.addActionListener(e -> loadData());
        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEdit());
        del.addActionListener(e -> onDelete());
    }

    private void loadCombos() {
        cbGroup.removeAllItems();
        for (Group g : groupRepo.findAll()) cbGroup.addItem(g);
        cbSubject.removeAllItems();
        for (Subject s : subjectRepo.findAll()) cbSubject.addItem(s);
    }

    private void loadData() {
        Group g = (Group) cbGroup.getSelectedItem();
        Subject s = (Subject) cbSubject.getSelectedItem();
        String sem = tfSemester.getText().trim();
        model.setRowCount(0);
        List<Assessment> list = service.listForProfessor(professor_id,
                g!=null? g.getId():null,
                s!=null? s.getSubjectId():null,
                null, null,
                sem.isBlank()? null : sem);
        for (Assessment a : list) {
            model.addRow(new Object[]{
                    a.getAssessmentId(),
                    a.getStudentId(),
                    a.getSubjectId(),
                    a.getGrade(),
                    a.getDate(),
                    a.getSemester(),
                    a.getComment()
            });
        }
    }

    private void onAdd() {
        Group g = (Group) cbGroup.getSelectedItem();
        Subject s = (Subject) cbSubject.getSelectedItem();
        if (g==null || s==null){ msg("Pasirinkite grupę ir dalyką"); return; }

        DefaultComboBoxModel<Student> dm = new DefaultComboBoxModel<>();
        for (Student st : studentRepo.findAll()) dm.addElement(st);
        JComboBox<Student> cbStud = new JComboBox<>(dm);

        JTextField tfGrade = new JTextField();
        JTextField tfDate  = new JTextField(LocalDate.now().toString());
        JTextField tfSem   = new JTextField(tfSemester.getText().trim());
        JTextField tfCom   = new JTextField();

        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "Studentas:", cbStud,
                "Pažymys (1-10):", tfGrade,
                "Data (YYYY-MM-DD):", tfDate,
                "Semestras:", tfSem,
                "Komentaras:", tfCom
        }, "Naujas pažymys", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                Student st = (Student) cbStud.getSelectedItem();
                int grade = Integer.parseInt(tfGrade.getText().trim());
                LocalDate dt = LocalDate.parse(tfDate.getText().trim());
                Assessment a = new Assessment(null, st.getStudentId(), s.getSubjectId(), professor_id,
                        grade, dt, tfCom.getText(), tfSem.getText().trim().isBlank()? null : tfSem.getText().trim());
                service.save(a);
                loadData();
            } catch (Exception ex) {
                msg("Klaida: " + ex.getMessage());
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow(); if (row==-1){ msg("Pasirinkite įrašą"); return; }
        Integer id = (Integer) model.getValueAt(row, 0);
        int curStud = (int) model.getValueAt(row, 1);
        int curSubj = (int) model.getValueAt(row, 2);
        int curGrade= (int) model.getValueAt(row, 3);
        String curDate = String.valueOf(model.getValueAt(row,4));
        String curSem  = String.valueOf(model.getValueAt(row,5));
        String curCom  = String.valueOf(model.getValueAt(row,6));

        JTextField tfGrade = new JTextField(String.valueOf(curGrade));
        JTextField tfDate  = new JTextField(curDate);
        JTextField tfSem   = new JTextField(curSem);
        JTextField tfCom   = new JTextField(curCom);

        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "Pažymys (1-10):", tfGrade,
                "Data (YYYY-MM-DD):", tfDate,
                "Semestras:", tfSem,
                "Komentaras:", tfCom
        }, "Redaguoti pažymį", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                int grade = Integer.parseInt(tfGrade.getText().trim());
                LocalDate dt = LocalDate.parse(tfDate.getText().trim());
                Assessment a = new Assessment(id, curStud, curSubj, professor_id, grade, dt, tfCom.getText(), tfSem.getText().trim());
                service.save(a);
                loadData();
            } catch (Exception ex) {
                msg("Klaida: " + ex.getMessage());
            }
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow(); if (row==-1){ msg("Pasirinkite įrašą"); return; }
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Ištrinti pažymį?", "Patvirtinimas",
                JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            service.delete(id);
            loadData();
        }
    }

    private void msg(String s){ JOptionPane.showMessageDialog(this, s); }
}
