import javax.swing.*;
import java.awt.*;

public class Student_frame extends JFrame {
    public Student_frame(User user) {
        super("Studentas");
        setSize(400, 200);
        setLocationRelativeTo(null);
        add(new JLabel("Sveiki, " + user.displayInfo(), SwingConstants.CENTER), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
