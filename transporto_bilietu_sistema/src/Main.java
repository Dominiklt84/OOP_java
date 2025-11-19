import ui.Main_frame;
import javax.swing.*;

public class Main{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main_frame frame = new Main_frame();
            frame.setVisible(true);
        });
    }
}