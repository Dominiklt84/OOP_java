import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class Login_frame extends JFrame {

    private final JTextField tfLogin = new JTextField(18);
    private final JPasswordField tfPassword = new JPasswordField(18);
    private final JButton btnLogin = new JButton("Prisijungti");

    private final User_repository userRepo = new JDBC_user_repository();

    public Login_frame() {
        super("Akademinė IS – prisijungimas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 220);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0; gc.gridy = 0; p.add(new JLabel("Naudotojo vardas:"), gc);
        gc.gridx = 1; p.add(tfLogin, gc);
        gc.gridx = 0; gc.gridy = 1; p.add(new JLabel("Slaptažodis:"), gc);
        gc.gridx = 1; p.add(tfPassword, gc);

        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2;
        gc.fill = GridBagConstraints.NONE; gc.anchor = GridBagConstraints.CENTER;
        p.add(btnLogin, gc);

        add(p);

        getRootPane().setDefaultButton(btnLogin);

        btnLogin.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String login = tfLogin.getText().trim();
        String password = new String(tfPassword.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Įveskite prisijungimo vardą ir slaptažodį.");
            return;
        }

        btnLogin.setEnabled(false);
        try {
            Optional<User> opt = userRepo.findByCredentials(login, password);
            if (opt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Neteisingi prisijungimo duomenys.");
                return;
            }

            User u = opt.get();

            if (u instanceof Admin) {
                JFrame f = new Admin_frame(u);
                f.setVisible(true);
                dispose();
            } else if (u instanceof Professor) {
                JFrame f = new Professor_frame(u);
                f.setVisible(true);
                dispose();
            } else if (u instanceof Student) {
                JFrame f = new Student_frame(u);
                f.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Nežinoma naudotojo rolė: " + u.getRole());
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Prisijungimo klaida: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            btnLogin.setEnabled(true);
        }
    }
}
