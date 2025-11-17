import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grades_viewer extends JPanel {

    private final int student_id;
    private final Grade_service service = new Grade_service();
    private final JDBC_subject_repository subjectRepo = new JDBC_subject_repository();

    private final JComboBox<Subject> cbSubject = new JComboBox<>();
    private final DefaultComboBoxModel<SemType> semModel = new DefaultComboBoxModel<>();
    private final JComboBox<SemType> cbSem = new JComboBox<>(semModel);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Dalykas", "Pažymys", "Įvesta", "Semestras", "Komentaras",
                    "subject_id", "sem_type_id"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    private final Map<Integer, String> subjectNames = new HashMap<>();
    private final Map<Integer, String> semTypeNames = new HashMap<>();

    public static class SemType {
        public final int id; public final String title;
        public SemType(int id, String title){ this.id=id; this.title=title; }
        public String toString(){ return title; }
    }

    public Grades_viewer(int student_id) {
        this.student_id = student_id;
        setLayout(new BorderLayout());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("Dalykas:"));   filters.add(cbSubject);
        filters.add(new JLabel("Semestras:")); filters.add(cbSem);
        JButton btnLoad = new JButton("Įkelti"); filters.add(btnLoad);
        add(filters, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        hideColumn(5);
        hideColumn(6);

        loadSubjects();
        loadSemTypes();
        preloadSubjectNames();

        btnLoad.addActionListener(e -> loadData());

        loadData();
    }

    private void loadSubjects() {
        cbSubject.removeAllItems();
        for (Subject s : subjectRepo.findAll()) cbSubject.addItem(s);
    }

    private void loadSemTypes() {
        semModel.removeAllElements();
        semTypeNames.clear();

        semModel.addElement(new SemType(-1, "(visi)"));

        try (Connection c = Data_base.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT sem_type_id, title FROM semester_type ORDER BY sem_type_id")) {
            while (rs.next()) {
                SemType t = new SemType(rs.getInt(1), rs.getString(2));
                semModel.addElement(t);
                semTypeNames.put(t.id, t.title);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Nepavyko įkelti semestrų: " + e.getMessage());
        }
        cbSem.setSelectedIndex(0);
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

    private void loadData() {
        model.setRowCount(0);

        Subject subj = (Subject) cbSubject.getSelectedItem();
        SemType sem = (SemType) cbSem.getSelectedItem();

        Integer subjectId = subj != null ? subj.getSubjectId() : null;
        Integer semTypeId = (sem != null && sem.id != -1) ? sem.id : null;

        List<Assessment> list = service.listForStudent(student_id, subjectId, semTypeId);

        for (Assessment a : list) {
            String subjName = subjectNames.getOrDefault(a.getSubjectId(), "subj:" + a.getSubjectId());
            String semTitle = a.getSemTypeId()==null ? "" : semTypeNames.getOrDefault(a.getSemTypeId(), String.valueOf(a.getSemTypeId()));
            String introduced = (a.getIntroduced()==null) ? "" : a.getIntroduced().toString();

            model.addRow(new Object[]{
                    subjName,
                    a.getWorth(),
                    introduced,
                    semTitle,
                    a.getComment(),
                    a.getSubjectId(),
                    a.getSemTypeId()
            });
        }
    }

    private void hideColumn(int idx){
        table.getColumnModel().getColumn(idx).setMinWidth(0);
        table.getColumnModel().getColumn(idx).setMaxWidth(0);
        table.getColumnModel().getColumn(idx).setWidth(0);
    }
}
