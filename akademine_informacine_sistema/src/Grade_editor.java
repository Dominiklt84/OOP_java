import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class Grade_editor extends JPanel {

    private final JDBC_group_repository groupRepo = new JDBC_group_repository();
    private final JDBC_subject_repository subjectRepo = new JDBC_subject_repository();
    private final JDBC_student_repository studentRepo = new JDBC_student_repository();
    private final JDBC_assessment_repository assessmentRepo = new JDBC_assessment_repository();

    private final JComboBox<Group> cbGroup = new JComboBox<>();
    private final JComboBox<Subject> cbSubject = new JComboBox<>();
    private final JComboBox<SemType> cbSemFilter = new JComboBox<>();
    private final DefaultComboBoxModel<SemType> semTypesModel = new DefaultComboBoxModel<>();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Studentas", "Dalykas", "Pažymys", "Įvesta", "Semestras", "Komentaras", "student_id", "subject_id", "sem_type_id"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    private Map<Integer, String> subjectNames = new HashMap<>();
    private Map<Integer, String> studentNames = new HashMap<>();
    private Map<Integer, String> semTypeNames = new HashMap<>();

    private final Integer professor_id;

    public static class SemType {
        public final int id; public final String title;
        public SemType(int id, String title) { this.id = id; this.title = title; }
        public String toString() { return title; }
    }

    public Grade_editor(Integer professor_id) {
        this.professor_id = professor_id;
        setLayout(new BorderLayout());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("Grupė:"));   filters.add(cbGroup);
        filters.add(new JLabel("Dalykas:")); filters.add(cbSubject);
        filters.add(new JLabel("Semestras:")); filters.add(cbSemFilter);
        add(filters, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton add = new JButton("Pridėti");
        JButton edit = new JButton("Redaguoti");
        JButton del = new JButton("Šalinti");
        JButton btnLoad = new JButton("Įkelti"); filters.add(btnLoad);
        actions.add(add); actions.add(edit); actions.add(del);
        add(actions, BorderLayout.SOUTH);

        loadGroups();
        loadSubjects();
        loadSemTypes();
        cbSemFilter.setModel(semTypesModel);
        cbGroup.addActionListener(e -> preloadStudentNames());
        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEdit());
        del.addActionListener(e -> onDelete());

        hideColumn(7);
        hideColumn(8);
        hideColumn(9);
        btnLoad.addActionListener(e -> loadData());

        loadData();
    }

    private void loadGroups() {
        cbGroup.removeAllItems();
        for (Group g : groupRepo.findAll()) cbGroup.addItem(g);
    }

    private void loadSubjects() {
        cbSubject.removeAllItems();
        for (Subject s : subjectRepo.findAll()) cbSubject.addItem(s);
    }

    private void loadSemTypes() {
        semTypesModel.removeAllElements();
        semTypeNames.clear();
        try (Connection c = Data_base.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT sem_type_id, title FROM semester_type ORDER BY sem_type_id")) {
            while (rs.next()) {
                SemType t = new SemType(rs.getInt(1), rs.getString(2));
                semTypesModel.addElement(t);
                semTypeNames.put(t.id, t.title);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Nepavyko įkelti semestrų: " + e.getMessage());
        }
        cbSemFilter.insertItemAt(new SemType(-1, "(visi)"), 0);
        cbSemFilter.setSelectedIndex(0);
    }

    private void preloadNames() {
        preloadSubjectNames();
        preloadStudentNames();
    }

    private void preloadSubjectNames() {
        subjectNames.clear();
        String sql = """
            SELECT s.subject_id, st.title
            FROM subject s
            JOIN subject_type st ON st.sub_type_id = s.sub_type_id
            """;
        try (Connection c = Data_base.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) subjectNames.put(rs.getInt(1), rs.getString(2));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Nepavyko įkelti dalykų pavadinimų: " + e.getMessage());
        }
    }

    private void preloadStudentNames() {
        studentNames.clear();
        Group g = (Group) cbGroup.getSelectedItem();
        try {
            List<Student> list = (g != null)
                    ? studentRepo.findByGroup(g.getId())
                    : studentRepo.findAll();
            for (Student s : list) {
                studentNames.put(s.getStudentId(), s.getFirstName() + " " + s.getLastName());
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Nepavyko įkelti studentų: " + ex.getMessage());
        }
    }

    private void loadData() {
        model.setRowCount(0);
        preloadNames();

        Group g = (Group) cbGroup.getSelectedItem();
        Subject s = (Subject) cbSubject.getSelectedItem();
        SemType sem = (SemType) cbSemFilter.getSelectedItem();

        Integer groupId   = g != null   && g.getId() > 0         ? g.getId()   : null;
        Integer subjectId = s != null                           ? s.getSubjectId() : null;
        Integer semTypeId = (sem != null && sem.id != -1)        ? sem.id      : null;

        List<Assessment> list = assessmentRepo.findByProfessorAndFilters(
                professor_id, groupId, subjectId, semTypeId
        );

        for (Assessment a : list) {
            String stud = studentNames.getOrDefault(a.getStudentId(), "stud:" + a.getStudentId());
            String subj = subjectNames.getOrDefault(a.getSubjectId(), "subj:" + a.getSubjectId());
            String semTitle = a.getSemTypeId()==null ? "" : semTypeNames.getOrDefault(a.getSemTypeId(), String.valueOf(a.getSemTypeId()));
            LocalDateTime ts = a.getIntroduced();
            model.addRow(new Object[]{
                    a.getAssessmentId(),
                    stud,
                    subj,
                    a.getWorth(),
                    ts == null ? "" : ts.toString(),
                    semTitle,
                    a.getComment(),
                    a.getStudentId(),
                    a.getSubjectId(),
                    a.getSemTypeId()
            });
        }
    }

    private void onAdd() {
        Group g = (Group) cbGroup.getSelectedItem();
        Subject subj = (Subject) cbSubject.getSelectedItem();
        if (g == null || subj == null) {
            msg("Pasirinkite grupę ir dalyką.");
            return;
        }

        DefaultComboBoxModel<Student> dm = new DefaultComboBoxModel<>();
        for (Student st : studentRepo.findByGroup(g.getId())) dm.addElement(st);
        JComboBox<Student> cbStud = new JComboBox<>(dm);

        JComboBox<SemType> cbSem = new JComboBox<>(semTypesModel);

        JTextField tfWorth = new JTextField();
        JTextField tfComment = new JTextField();

        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "Studentas:", cbStud,
                "Pažymys (worth, pvz. 8.5):", tfWorth,
                "Semestras:", cbSem,
                "Komentaras:", tfComment
        }, "Naujas pažymys", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                Student st = (Student) cbStud.getSelectedItem();
                double worth = Double.parseDouble(tfWorth.getText().trim());
                SemType semSel = (SemType) cbSem.getSelectedItem();

                Assessment a = new Assessment(
                        null,
                        st.getStudentId(),
                        subj.getSubjectId(),
                        professor_id,
                        worth,
                        semSel != null ? semSel.id : null,
                        tfComment.getText().trim(),
                        null
                );

                assessmentRepo.save(a);
                loadData();
            } catch (Exception ex) {
                msg("Klaida: Įveskite pažymį." );
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) { msg("Pasirinkite įrašą"); return; }

        Integer id = (Integer) model.getValueAt(row, 0);
        int studId = (int) model.getValueAt(row, 7);
        int subjId = (int) model.getValueAt(row, 8);
        Integer semTypeId = (Integer) model.getValueAt(row, 9);

        String curWorth = String.valueOf(model.getValueAt(row, 3));
        String curComment = String.valueOf(model.getValueAt(row, 6));

        JTextField tfWorth = new JTextField(curWorth);
        JTextField tfComment = new JTextField(curComment);

        JComboBox<SemType> cbSem = new JComboBox<>(semTypesModel);
        if (semTypeId != null) {
            for (int i = 0; i < semTypesModel.getSize(); i++) {
                if (semTypesModel.getElementAt(i).id == semTypeId) { cbSem.setSelectedIndex(i); break; }
            }
        }

        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "Pažymys (worth):", tfWorth,
                "Semestras:", cbSem,
                "Komentaras:", tfComment
        }, "Redaguoti pažymį", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                double worth = Double.parseDouble(tfWorth.getText().trim());
                SemType semSel = (SemType) cbSem.getSelectedItem();

                Assessment a = new Assessment(
                        id, studId, subjId, professor_id,
                        worth,
                        semSel != null ? semSel.id : null,
                        tfComment.getText().trim(),
                        null
                );
                assessmentRepo.save(a);
                loadData();
            } catch (Exception ex) {
                msg("Klaida: " + ex.getMessage());
            }
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) { msg("Pasirinkite įrašą"); return; }
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Ištrinti pažymį?", "Patvirtinimas",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            assessmentRepo.delete(id);
            loadData();
        }
    }

    private void hideColumn(int idx) {
        table.getColumnModel().getColumn(idx).setMinWidth(0);
        table.getColumnModel().getColumn(idx).setMaxWidth(0);
        table.getColumnModel().getColumn(idx).setWidth(0);
    }

    private void msg(String s) { JOptionPane.showMessageDialog(this, s); }
}
