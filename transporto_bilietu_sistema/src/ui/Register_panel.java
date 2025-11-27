package ui;
import user.User;
import service.Auth_service;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Register_panel extends JPanel {
    public interface RegisterListener {
        void onRegisterSuccess(User user);
        void onShowLogin();
    }

    private final Auth_service auth_service;
    private final RegisterListener listener;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public Register_panel(Auth_service auth_service, RegisterListener listener) {
        this.auth_service = auth_service;
        this.listener = listener;
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Register");
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

        JButton registerBtn = new JButton(new AbstractAction("Register") {
            public void actionPerformed(ActionEvent e) {
                doRegister();
            }
        });

        JButton backBtn = new JButton(new AbstractAction("Back to Login") {
            public void actionPerformed(ActionEvent e) {
                listener.onShowLogin();
            }
        });

        c.gridx = 0; c.gridy++;
        add(registerBtn, c);
        c.gridx = 1;
        add(backBtn, c);
    }

    private void doRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            User user = auth_service.registerClient(username, password);
            JOptionPane.showMessageDialog(this, "Registered successfully, you can login");
            listener.onRegisterSuccess(user);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
