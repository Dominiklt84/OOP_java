package ui;

import users.Auth_service;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Login_panel extends JPanel{
    public Login_panel(Auth_service auth_service, Runnable onLoginSuccess) {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Vartotojo vardas:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Slaptazodis:");
        JPasswordField passField = new JPasswordField(15);
        JButton loginBtn = new JButton("Prisijungti");
        JLabel status = new JLabel(" ");
        JButton registerBtn = new JButton("Registruotis");

        c.gridx = 0; c.gridy = 0;
        add(userLabel, c);
        c.gridx = 1;
        add(userField, c);

        c.gridx = 0; c.gridy = 1;
        add(passLabel, c);
        c.gridx = 1;
        add(passField, c);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        add(loginBtn, c);

        c.gridy = 3;
        add(status, c);

        c.gridy = 4;
        add(registerBtn, c);

        loginBtn.addActionListener(e -> {
            String u = userField.getText();
            String p = new String(passField.getPassword());
            if (auth_service.login(u, p)) {
                status.setText("Prisijungta sekmingai.");
                onLoginSuccess.run();
            } else {
                status.setText("Neteisingi duomenys.");
            }
        });

        registerBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                status.setText("Užpildykite laukus.");
                return;
            }
            if (((Auth_service)auth_service).registration(u, p)) {
                status.setText("Registracija sėkminga, prisijunkite.");
            } else {
                status.setText("Toks vartotojas jau yra.");
            }
        });
    }
}
