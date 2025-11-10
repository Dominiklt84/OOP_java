import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class Login_frame extends JFrame {
    private final User_repository repo = new User_repository();

    public Login_frame() {
        super("AIS – Prisijungimas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);

        JTextField loginField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JLabel info = new JLabel(" ");
        JButton loginBtn = new JButton("Prisijungti");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.gridx = 0; gc.gridy = 0; panel.add(new JLabel("Vartotojo vardas:"), gc);
        gc.gridx = 1; panel.add(loginField, gc);
        gc.gridx = 0; gc.gridy = 1; panel.add(new JLabel("Slaptažodis:"), gc);
        gc.gridx = 1; panel.add(passField, gc);
        gc.gridx = 1; gc.gridy = 2; panel.add(loginBtn, gc);
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2; panel.add(info, gc);
        add(panel);

        loginBtn.addActionListener(e -> {
            String login = loginField.getText();
            String pass = new String(passField.getPassword());
            Optional<User> u = repo.findByCredentials(login, pass);
            if (u.isEmpty()) {
                info.setForeground(Color.RED);
                info.setText("Neteisingi duomenys!");
            } else {
                info.setForeground(Color.GREEN);
                info.setText("Prisijungta kaip " + u.get().getRole());
                openRoleWindow(u.get());
            }
        });
    }

    private void openRoleWindow(User u) {
        dispose();
        switch (u.getRole()) {
            case "ADMIN" -> new AdminFrame(u).setVisible(true);
            case "PROFESSOR" -> new ProfessorFrame(u).setVisible(true);
            case "STUDENT" -> new StudentFrame(u).setVisible(true);
        }
    }
}
