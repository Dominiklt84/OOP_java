import javax.swing.*;
import java.awt.*;

public class Admin_frame extends JFrame {
    public Admin_frame(User user) {
        super("Administratorius – " + user.getFirstName());
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Grupės", new Groups_admin());
        tabs.addTab("Studentai", new Students_admin());
        tabs.addTab("Dėstytojai", new Professors_admin());
        tabs.addTab("Dalykai", new Subjects_admin());
        tabs.addTab("Priskyrimai", new Assignments_admin());

        add(tabs, BorderLayout.CENTER);
    }
}
