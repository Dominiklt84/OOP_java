import javax.swing.*;
import java.awt.*;

public class Admin_frame extends JFrame {

    public Admin_frame(User user) {
        super("Administratorius – " + user.getFirstName());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Grupės", new Groups_admin());
        tabs.addTab("Studentai", new Students_admin());
        tabs.addTab("Dėstytojai", new JLabel("Dėstytojų valdymas (bus vėliau)"));
        tabs.addTab("Dalykai", new JLabel("Dalykų valdymas (bus vėliau)"));
        add(tabs, BorderLayout.CENTER);
    }
}
