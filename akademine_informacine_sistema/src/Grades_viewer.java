import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Grades_viewer extends JPanel {
    private final Grade_service service = new Grade_service();
    private final JDBC_subject_repository subjectRepo = new JDBC_subject_repository();

    private final JComboBox<Subject> cbSubject = new JComboBox<>();
    private final JTextField tfSemester = new JTextField(10);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Dalykas","Pažymys","Data","Semestras","Komentaras"}, 0){
        public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable table = new JTable(model);

    private final int student_id;
    public Grades_viewer(int student_id) {
        this.student_id = student_id;
        setLayout(new BorderLayout());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("Dalykas:")); filters.add(cbSubject);
        filters.add(new JLabel("Semestras:")); filters.add(tfSemester);
        JButton btnLoad = new JButton("Įkelti"); filters.add(btnLoad);
        add(filters, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        for (Subject s : subjectRepo.findAll()) cbSubject.addItem(s);

        btnLoad.addActionListener(e -> loadData());
    }

    private void loadData() {
        Subject s = (Subject) cbSubject.getSelectedItem();
        String sem = tfSemester.getText().trim();
        model.setRowCount(0);
        List<Assessment> list = service.listForStudent(student_id,
                s!=null? s.getSubjectId():null,
                sem.isBlank()? null : sem);
        for (Assessment a : list) {
            model.addRow(new Object[]{a.getSubjectId(), a.getGrade(), a.getDate(), a.getSemester(), a.getComment()});
        }
    }
}
