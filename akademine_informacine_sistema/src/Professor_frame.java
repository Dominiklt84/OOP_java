import javax.swing.*;
import java.awt.*;

public class Professor_frame extends JFrame {
    public Professor_frame(User user) {
        super("Dėstytojas");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        add(new JLabel("Sveiki, " + user.displayInfo(), SwingConstants.CENTER), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Pažymiai", new Grade_editor( null));
        add(tabs, BorderLayout.CENTER);
    }
}
