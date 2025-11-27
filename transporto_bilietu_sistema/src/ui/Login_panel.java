package ui;
import user.User;
import service.Auth_service;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Login_panel extends JPanel {
    public interface LoginListener {
        void onLoginSuccess(User user);
        void onShowRegister();
    }

    private final Auth_service auth_service;
    private final LoginListener listener;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login_panel(Auth_service auth_service, LoginListener listener) {
        this.auth_service = auth_service;
        this.listener = listener;
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Login");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        add(title, c);

        c.gridwidth = 1;
        c.gridy++;
        add(new JLabel("Username:"), c);

        usernameField = new JTextField(15);
        c.gridx = 1;
        add(usernameField, c);

        c.gridx = 0; c.gridy++;
        add(new JLabel("Password:"), c);

        passwordField = new JPasswordField(15);
        c.gridx = 1;
        add(passwordField, c);

        JButton loginBtn = new JButton(new AbstractAction("Login") {
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        JButton registerBtn = new JButton(new AbstractAction("Register") {
            public void actionPerformed(ActionEvent e) {
                listener.onShowRegister();
            }
        });

        c.gridx = 0; c.gridy++;
        add(loginBtn, c);
        c.gridx = 1;
        add(registerBtn, c);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        auth_service.login(username, password).ifPresentOrElse(user -> {
            JOptionPane.showMessageDialog(this, "Welcome, " + user.getUsername());
            listener.onLoginSuccess(user);
        }, () -> JOptionPane.showMessageDialog(this, "Wrong username or password"));
    }
}
