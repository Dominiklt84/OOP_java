import javax.swing.*;
import java.awt.*;

public class Professor_frame extends JFrame {
    public Professor_frame(User user) {
        super("Dėstytojas");
        setSize(400, 200);
        setLocationRelativeTo(null);
        add(new JLabel("Sveiki, " + user.displayInfo(), SwingConstants.CENTER), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
